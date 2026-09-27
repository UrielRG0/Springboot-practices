package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tacos.PaymentMethod;
import tacos.TacoOrder;
import tacos.User;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = OrderReorderTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
public class OrderReorderTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @MockBean
    private tacos.web.api.OrderPricingService pricingService;

    @MockBean
    private tacos.InventoryService inventoryService;

    @MockBean
    private tacos.web.api.PaymentGateway paymentGateway;

    @MockBean
    private tacos.messaging.OrderMessagingService orderMessages;

    private User userA, userB;
    private TacoOrder oldOrder;

    @BeforeEach
    public void setupData() {
        mongoTemplate.remove(new Query(), TacoOrder.class).block();
        mongoTemplate.remove(new Query(), User.class).block();

        userA = new User("batman", "pass", "Bruce", "Cave", "Gotham", "NY", "123", "555", "b@dc.com", "ROLE_USER");
        userA.setId("U1");
        userB = new User("joker", "pass", "Jack", "Arkham", "Gotham", "NY", "321", "555", "j@dc.com", "ROLE_USER");
        userB.setId("U2");
        mongoTemplate.insertAll(Arrays.asList(userA, userB)).collectList().block();

        oldOrder = new TacoOrder();
        oldOrder.setId("ORD_ORIGINAL");
        oldOrder.setUser(userA);
        oldOrder.setTotal(new BigDecimal("15.00")); 
        oldOrder.setDeliveryName("Bruce Wayne");
        oldOrder.setPlacedAt(new Date(System.currentTimeMillis() - 86400000)); 
        mongoTemplate.save(oldOrder).block();

        when(paymentGateway.tokenize(any(), any())).thenReturn(Mono.just(new PaymentMethod()));
        when(inventoryService.reserveInventory(any())).thenReturn(Mono.empty());
    }

    // Ownsership
    @Test
    public void testReorder_AlienOrder_Returns404() throws Exception {
        String payload = "{\"paymentToken\": \"tok_new_123\", \"confirmPriceChange\": false}";

        MvcResult result = mockMvc.perform(post("/api/orders/ORD_ORIGINAL/reorder").with(user(userB)).contentType(MediaType.APPLICATION_JSON).content(payload)).andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isNotFound());
        } else {
            org.junit.jupiter.api.Assertions.assertEquals(404, result.getResponse().getStatus());
        }
    }

    // price change for the new order
    @Test
    public void testReorder_PriceChanged_Returns409Quote() throws Exception {
        when(pricingService.calculatePrices(any(TacoOrder.class))).thenAnswer(invocation -> {
            TacoOrder order = invocation.getArgument(0);
            order.setTotal(new BigDecimal("20.00")); 
            return Mono.just(order);
        });

        String payload = "{\"paymentToken\": \"tok_new_123\", \"confirmPriceChange\": false}";

        MvcResult result = mockMvc.perform(post("/api/orders/ORD_ORIGINAL/reorder")
                // Pasamos a Batman real
                .with(user(userA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isConflict()).andExpect(jsonPath("$.requiresConfirmation", is(true))).andExpect(jsonPath("$.oldTotal", is(15.0))).andExpect(jsonPath("$.newTotal", is(20.0)));
        }
    }

    // Stock conflict, without inventory
    @Test
    public void testReorder_OutOfStock_Returns409Conflict() throws Exception {
        when(pricingService.calculatePrices(any(TacoOrder.class))).thenAnswer(i -> {
            TacoOrder o = i.getArgument(0);
            o.setTotal(new BigDecimal("15.00"));
            return Mono.just(o);
        });
        
        when(inventoryService.reserveInventory(any())).thenReturn(
            Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK"))
        );

        String payload = "{\"paymentToken\": \"tok_new_123\", \"confirmPriceChange\": true}";

        MvcResult result = mockMvc.perform(post("/api/orders/ORD_ORIGINAL/reorder").with(user(userA)).contentType(MediaType.APPLICATION_JSON).content(payload)).andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isConflict()); 
        }
    }

    // New identity and new date
    @Test
    public void testReorder_Success_NewIdentityCreated() throws Exception {
        when(pricingService.calculatePrices(any(TacoOrder.class))).thenAnswer(i -> {
            TacoOrder o = i.getArgument(0);
            o.setTotal(new BigDecimal("15.00"));
            return Mono.just(o);
        });

        String payload = "{\"paymentToken\": \"tok_new_123\", \"confirmPriceChange\": false}";

        MvcResult result = mockMvc.perform(post("/api/orders/ORD_ORIGINAL/reorder")
                // Pasamos a Batman real
                .with(user(userA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isCreated()).andExpect(jsonPath("$.id", not("ORD_ORIGINAL"))) .andExpect(jsonPath("$.deliveryName", is("Bruce Wayne"))); 
        }
    }
}