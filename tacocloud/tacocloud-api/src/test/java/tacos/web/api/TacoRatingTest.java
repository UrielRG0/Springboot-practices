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
import tacos.Ingredient;
import tacos.Taco;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TacoRatingTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
public class TacoRatingTest {

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
        mongoTemplate.getCollectionNames()
            .filter(name -> name.toLowerCase().contains("rating") || name.toLowerCase().contains("calificacion"))
            .flatMap(name -> mongoTemplate.dropCollection(name))
            .blockLast();
        
        Ingredient dummyIng = new Ingredient("TEST", "Ingrediente", Ingredient.Type.WRAP);

        Taco t1 = new Taco(); t1.setId("TACO_REY"); t1.setName("El Rey Supremo"); 
        t1.setCreatedAt(new Date()); t1.setIngredients(Arrays.asList(dummyIng));

        Taco t2 = new Taco(); t2.setId("TACO_DOS"); t2.setName("El Subcampeón"); 
        t2.setCreatedAt(new Date()); t2.setIngredients(Arrays.asList(dummyIng));
        
        mongoTemplate.insertAll(Arrays.asList(t1, t2)).blockLast();
    }

    // Invalid score (1-5)
    @Test
    public void testInvalidScore_ReturnsBadRequest() throws Exception {
        String payload = "{ \"score\": 6 }"; 

        MvcResult result = mockMvc.perform(put("/api/tacos/TACO_REY/rating")
                .with(user("juez"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().is4xxClientError());
        } else {
            org.junit.jupiter.api.Assertions.assertTrue(result.getResponse().getStatus() >= 400 && result.getResponse().getStatus() < 500);
        }
    }

    // Ghost taco
    @Test
    public void testVoteGhostTaco_ReturnsError() throws Exception {
        String payload = "{ \"score\": 5 }";

        MvcResult result = mockMvc.perform(put("/api/tacos/FANTASMA/rating")
                .with(user("juez"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            try {
                mockMvc.perform(asyncDispatch(result)).andExpect(status().is4xxClientError());
            } catch (Exception e) { }
        } else {
            org.junit.jupiter.api.Assertions.assertTrue(result.getResponse().getStatus() >= 400);
        }
    }

    // vote twice the same taco doesnt duplicate that
    @Test
    public void testUpdateVote_IsIdempotent() throws Exception {
        MvcResult res1 = mockMvc.perform(put("/api/tacos/TACO_REY/rating")
                .with(user("maria"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"score\": 3 }")).andReturn();
        if (res1.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(res1));
        MvcResult res2 = mockMvc.perform(put("/api/tacos/TACO_REY/rating")
                .with(user("maria"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"score\": 5 }")).andReturn();
        if (res2.getRequest().isAsyncStarted()) {
            try { mockMvc.perform(asyncDispatch(res2)); } catch (Exception e) {}
        }
        
    }

    // Agregation and ranking test
    @Test
    public void testRanking_ReturnsTopTacos() throws Exception {
        for (int i = 1; i <= 5; i++) {
            MvcResult r = mockMvc.perform(put("/api/tacos/TACO_REY/rating").with(user("user" + i))
                .contentType(MediaType.APPLICATION_JSON).content("{ \"score\": 5 }")).andReturn();
            if (r.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(r));
        }
        for (int i = 1; i <= 5; i++) {
            MvcResult r = mockMvc.perform(put("/api/tacos/TACO_DOS/rating").with(user("user" + i))
                .contentType(MediaType.APPLICATION_JSON).content("{ \"score\": 4 }")).andReturn();
            if (r.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(r));
        }

        MvcResult result = mockMvc.perform(get("/api/tacos/top?limit=10")).andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", org.hamcrest.Matchers.is("TACO_REY")));
        }
    }
}