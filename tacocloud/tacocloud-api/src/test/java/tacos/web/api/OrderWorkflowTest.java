package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
//import reactor.core.publisher.Mono;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = OrderWorkflowTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
public class OrderWorkflowTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private OrderRepository orderRepo;
    @MockBean private tacos.web.api.OrderPricingService pricingService;
    @MockBean private tacos.InventoryService inventoryService;
    @MockBean private tacos.web.api.PaymentGateway paymentGateway;
    @MockBean private tacos.messaging.OrderMessagingService orderMessages;
    @MockBean private tacos.web.api.EmailOrderService emailOrderService;

    private User customer, admin;
    private TacoOrder testOrder;

    @BeforeEach
    public void setup() {
        mongoTemplate.remove(new Query(), TacoOrder.class).block();
        mongoTemplate.remove(new Query(), User.class).block();

        customer = new User("batman", "pass", "Bruce", "Cave", "Gotham", "NY", "123", "55", "b@dc.com", "ROLE_USER");
        customer.setId("U1");

        admin = new User("alfred", "pass", "Alfred", "Manor", "Gotham", "NY", "321", "55", "a@dc.com", "ROLE_ADMIN");
        admin.setId("U2");

        mongoTemplate.insertAll(Arrays.asList(customer, admin)).collectList().block();

        testOrder = new TacoOrder();
        testOrder.setId("ORD_WF_1");
        testOrder.setUser(customer);
        testOrder.setStatus(OrderStatus.CREATED);
        mongoTemplate.save(testOrder).block();
    }

    // Succesfull transicion
    @Test
    public void testAdminAdvancesStatus_Success() throws Exception {
        String payload = "{\"status\": \"ACCEPTED\", \"reason\": \"Cocina inicia preparación\"}";

        MvcResult result = mockMvc.perform(patch("/api/orders/ORD_WF_1/status")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
        TacoOrder dbOrder = orderRepo.findById("ORD_WF_1").block();
        assertEquals(OrderStatus.ACCEPTED, dbOrder.getStatus());
        assertEquals(1, dbOrder.getStatusHistory().size());
        assertEquals("Cocina inicia preparación", dbOrder.getStatusHistory().get(0).getReason());
        assertEquals("alfred", dbOrder.getStatusHistory().get(0).getUsername());
    }

    //Invalid transicion
    @Test
    public void testInvalidTransition_ThrowsConflict() throws Exception {
        String payload = "{\"status\": \"DELIVERED\", \"reason\": \"Entrega flash\"}";

        MvcResult result = mockMvc.perform(patch("/api/orders/ORD_WF_1/status")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isConflict());
    }

    // a user cant see the order one time this is ordered
    @Test
    public void testCustomerCannotAdvanceStatus_Forbidden() throws Exception {
        String payload = "{\"status\": \"ACCEPTED\"}";

        MvcResult result = mockMvc.perform(patch("/api/orders/ORD_WF_1/status")
                .with(user(customer)) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isForbidden());
    }

    // User cancel succesfully
    @Test
    public void testCustomerCancelsOrder_Success() throws Exception {
        String payload = "{\"reason\": \"Me arrepentí\"}";

        MvcResult result = mockMvc.perform(post("/api/orders/ORD_WF_1/cancel")
                .with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    //Lately cancelation
    @Test
    public void testCustomerCancelsOrderTooLate_Conflict() throws Exception {
        testOrder.setStatus(OrderStatus.PREPARING);
        mongoTemplate.save(testOrder).block();

        MvcResult result = mockMvc.perform(post("/api/orders/ORD_WF_1/cancel")
                .with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isConflict());
    }

    //OPTIMISTIC LOCKING
    @Test
    public void testOptimisticLocking_ConcurrentModification() {
        TacoOrder thread1 = orderRepo.findById("ORD_WF_1").block();
        TacoOrder thread2 = orderRepo.findById("ORD_WF_1").block();
        thread1.setStatus(OrderStatus.ACCEPTED);
        orderRepo.save(thread1).block();
        thread2.setStatus(OrderStatus.CANCELLED);
        assertThrows(OptimisticLockingFailureException.class, () -> {
            orderRepo.save(thread2).block();
        });
    }
}