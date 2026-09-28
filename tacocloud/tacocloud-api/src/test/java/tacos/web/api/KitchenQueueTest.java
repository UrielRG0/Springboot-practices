package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
//import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = KitchenQueueTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
public class KitchenQueueTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private User customer, cook;
    private TacoOrder order1, order2;

    @BeforeEach
    public void setup() {

        mongoTemplate.remove(new Query(), TacoOrder.class).block();
        mongoTemplate.remove(new Query(), User.class).block();

        customer = new User("batman", "pass", "Bruce", "Cave", "Gotham", "NY", "123", "55", "b@dc.com", "ROLE_USER");
        customer.setId("U1");

        cook = new User("gordon", "pass", "Jim", "GCPD", "Gotham", "NY", "321", "55", "j@dc.com", "ROLE_KITCHEN");
        cook.setId("U2");
        mongoTemplate.insertAll(Arrays.asList(customer, cook)).collectList().block();

        order1 = new TacoOrder();
        order1.setId("ORD_VIEJA");
        order1.setStatus(tacos.OrderStatus.CREATED);
        order1.setPlacedAt(new Date(System.currentTimeMillis() - 10000)); 
        
        order2 = new TacoOrder();
        order2.setId("ORD_NUEVA");
        order2.setStatus(tacos.OrderStatus.CREATED);
        order2.setPlacedAt(new Date()); 
        mongoTemplate.insertAll(Arrays.asList(order1, order2)).collectList().block();
    }

    // Roles and security
    @Test
    public void testCustomerCannotAccessKitchen_Forbidden() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/kitchen/queue")
                .with(user(customer)) 
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        org.junit.jupiter.api.Assertions.assertEquals(403, result.getResponse().getStatus());
    }

    // The tale return the orders based in his aniquetie
    @Test
    public void testKitchenQueue_ReturnsFIFO() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/kitchen/queue")
                .with(user(cook)) // Entra Gordon (Cocinero)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("ORD_VIEJA"))) 
                .andExpect(jsonPath("$[1].id", is("ORD_NUEVA"))); 
        }
    }

    // The coocker get the order more older
    @Test
    public void testClaimOrder_CalculatesEtaAndSetsCook() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/kitchen/orders/claim")
                .with(user(cook))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("ORD_VIEJA"))) 
                .andExpect(jsonPath("$.status", is("ACCEPTED"))) 
                .andExpect(jsonPath("$.cookId", is("gordon"))) 
                .andExpect(jsonPath("$.estimatedPrepMinutes", greaterThan(0))); 
        }
    }

    // Tale empty
    @Test
    public void testClaimOrder_EmptyQueue_Returns404() throws Exception {
        mongoTemplate.remove(new Query(), TacoOrder.class).block();

        MvcResult result = mockMvc.perform(post("/api/kitchen/orders/claim")
                .with(user(cook))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isNotFound()); 
        }
    }
}