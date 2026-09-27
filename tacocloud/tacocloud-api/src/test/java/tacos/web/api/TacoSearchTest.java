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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tacos.Taco;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TacoSearchTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class TacoSearchTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @BeforeEach
    public void setupData() {
        mongoTemplate.remove(Taco.class).all().block();

        Taco t1 = new Taco();
        t1.setId("t1");
        t1.setName("Fuego Vegano");
        t1.setCreatedAt(new Date(System.currentTimeMillis() - 10000)); // 10 s created

        Taco t2 = new Taco();
        t2.setId("t2");
        t2.setName("Carnita Suave");
        t2.setCreatedAt(new Date(System.currentTimeMillis() - 5000)); // 5 s created

        Taco t3 = new Taco();
        t3.setId("t3");
        t3.setName("Clásico");
        t3.setCreatedAt(new Date()); 

        mongoTemplate.insertAll(Arrays.asList(t1, t2, t3)).blockLast();
    }

    // Void query
    @Test
    public void testEmptyQuery_ReturnsFirstPageWithDefaultSize() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tacos")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(3))) .andExpect(jsonPath("$.page", is(0))).andExpect(jsonPath("$.totalElements", is(3)));
    }

    // Search by name
    @Test
    public void testFilterByName_ReturnsMatchingTacos() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tacos?name=Fuego")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1))).andExpect(jsonPath("$.content[0].name", is("Fuego Vegano")));
    }

    // Size Excesive is limited or denied
    @Test
    public void testExcessiveSize_IsLimitedToMaxSafeSize() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tacos?page=0&size=5000")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", org.hamcrest.Matchers.lessThanOrEqualTo(50))); 
        } else {
            int statusCode = result.getResponse().getStatus();
            org.junit.jupiter.api.Assertions.assertTrue(
                statusCode >= 400, 
                "Error: " + statusCode + ")"
            );
        }
    }

    // Sort por createdAt descendente
    @Test
    public void testSortByDate_ReturnsTacosInOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tacos?sort=createdAt,desc")
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].name", is("Clásico"))).andExpect(jsonPath("$.content[2].name", is("Fuego Vegano")));
    }
}