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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tacos.PaymentMethod;
import tacos.TacoOrder;
import tacos.User;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = OrderHistoryTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
public class OrderHistoryTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private User userA, userB;
    private TacoOrder orderA, orderB;

    @BeforeEach
    public void setupData() {
        mongoTemplate.remove(TacoOrder.class).all().block();
        mongoTemplate.remove(User.class).all().block();
        userA = new User("batman", "pass", "Bruce Wayne", "Cave St", "Gotham", "NY", "12345", "555-0000", "batman@dc.com", "ROLE_USER");
        userA.setId("U1"); 

        userB = new User("joker", "pass", "Jack Napier", "Arkham St", "Gotham", "NY", "54321", "555-1111", "joker@dc.com", "ROLE_USER");
        userB.setId("U2");

        mongoTemplate.insertAll(Arrays.asList(userA, userB)).blockLast();

        PaymentMethod pm = new PaymentMethod();

        orderA = new TacoOrder();
        orderA.setId("ORD_A"); orderA.setUser(userA); orderA.setTotal(new BigDecimal("15.00"));
        orderA.setPaymentMethod(pm); orderA.setPlacedAt(new Date(System.currentTimeMillis() - 10000));

        orderB = new TacoOrder();
        orderB.setId("ORD_B"); orderB.setUser(userB); orderB.setTotal(new BigDecimal("20.00"));
        orderB.setPaymentMethod(pm); orderB.setPlacedAt(new Date()); 

        mongoTemplate.insertAll(Arrays.asList(orderA, orderB)).blockLast();
    }

    // A cant see b and b canot se a
    @Test
    public void testIsolation_UserACannotSeeUserBOrders_Returns404() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/me/orders/ORD_B")
                .with(user("batman").roles("USER")))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isNotFound());
        } else {
            org.junit.jupiter.api.Assertions.assertEquals(404, result.getResponse().getStatus());
        }
    }

    // Stabe order and privacity in the historial
    @Test
    public void testPaginationAndOrder_StableSort() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/me/orders?page=0&size=5").with(user("joker").roles("USER"))).andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is("ORD_B")));
        }
    }

    // PaymenthMetodh and user are excluded
    @Test
    public void testSafeJson_DoesNotExposeSensitiveData() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/me/orders/ORD_A")
                .with(user("batman").roles("USER")))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is("ORD_A"))).andExpect(jsonPath("$.total", is(15.0))) .andExpect(jsonPath("$.user").doesNotExist())         .andExpect(jsonPath("$.paymentMethod").doesNotExist()); 
        }
    }

    // Admin Endpoint
    @Test
    public void testAdmin_UsesExplicitEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/orders")
                .with(user("alfred").roles("ADMIN")))
                .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))); 
        }
    }
}