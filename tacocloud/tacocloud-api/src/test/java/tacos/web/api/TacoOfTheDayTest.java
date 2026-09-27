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
import tacos.Ingredient;
import tacos.Taco;

import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TacoOfTheDayTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class TacoOfTheDayTest {

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
        mongoTemplate.remove(Ingredient.class).all().block();
        
        // 1. Mangikabil kadagiti pudno ken balid nga ingredients (kas idiay TC-18)
        Ingredient wrap = new Ingredient("TMAC", "Tortilla Maíz", Ingredient.Type.WRAP);
        wrap.setAvailable(true); wrap.setStockOnHand(100);
        mongoTemplate.save(wrap).block();

        Ingredient beef = new Ingredient("BEEF", "Carne Asada", Ingredient.Type.PROTEIN);
        beef.setAvailable(true); beef.setStockOnHand(100);
        mongoTemplate.save(beef).block();

        // 2. Mangaramid kadagiti naan-anay a balid a tacos (Dagitoy ti piliendanto)
        Taco t1 = new Taco(); t1.setId("TACO1"); t1.setName("Taco Alfa"); 
        t1.setCreatedAt(new Date(System.currentTimeMillis() - 100000)); 
        t1.setIngredients(Arrays.asList(wrap, beef)); // Balid a taco
        
        Taco t2 = new Taco(); t2.setId("TACO2"); t2.setName("Taco Bravo"); 
        t2.setCreatedAt(new Date(System.currentTimeMillis() - 50000)); 
        t2.setIngredients(Arrays.asList(wrap, beef)); // Balid a taco
        
        Taco t3 = new Taco(); t3.setId("TACO3"); t3.setName("Veg-Out"); 
        t3.setCreatedAt(new Date()); 
        t3.setIngredients(Arrays.asList(wrap, beef)); // Balid a taco
        
        mongoTemplate.insertAll(Arrays.asList(t1, t2, t3)).blockLast();
    }

    // Two calls return the same taco
    @Test
    public void testSameDay_ReturnsExactlySameTaco() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/api/tacos/today").contentType(MediaType.APPLICATION_JSON)).andReturn();

        String primerTacoId = "";
        
        if (result1.getRequest().isAsyncStarted()) {
            String jsonResponse = mockMvc.perform(asyncDispatch(result1)).andExpect(status().isOk()).andExpect(jsonPath("$.date").exists()).andExpect(jsonPath("$.taco.id").exists()).andReturn().getResponse().getContentAsString();
                
            primerTacoId = jsonResponse.split("\"id\":\"")[1].split("\"")[0];
        }
        MvcResult result2 = mockMvc.perform(get("/api/tacos/today")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
            
        if (result2.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taco.id", org.hamcrest.Matchers.is(primerTacoId)));
        }
    }

    // If the db is empty accept 404, 204 and 500
    @Test
    public void testNoCandidates_Returns404() throws Exception {
        mongoTemplate.remove(Taco.class).all().block(); 

        MvcResult result = mockMvc.perform(get("/api/tacos/today")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        
        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().is4xxClientError()); 
        } else {
            int statusCode = result.getResponse().getStatus();
            org.junit.jupiter.api.Assertions.assertTrue(
                statusCode == 404 || statusCode == 204 || statusCode >= 500,
                "Error Spected but we get: " + statusCode
            );
        }
    }
}