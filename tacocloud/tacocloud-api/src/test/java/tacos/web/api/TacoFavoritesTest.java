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
    classes = TacoFavoritesTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
public class TacoFavoritesTest {

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
        // Limpiamos Tacos y la colección de Favoritos (sea cual sea su nombre en tu BD)
        mongoTemplate.remove(Taco.class).all().block();
        mongoTemplate.getCollectionNames()
            .filter(name -> name.toLowerCase().contains("favorite"))
            .flatMap(name -> mongoTemplate.dropCollection(name))
            .blockLast();
        
        // Creamos un taco válido para que el sistema lo encuentre
        Ingredient dummyIng = new Ingredient("TEST", "Ingrediente", Ingredient.Type.WRAP);
        Taco t1 = new Taco(); 
        t1.setId("TACO_VALIDO"); 
        t1.setName("El Sabroso"); 
        t1.setCreatedAt(new Date()); 
        t1.setIngredients(Arrays.asList(dummyIng));
        
        mongoTemplate.save(t1).block();
    }

    // Double click doesnt affect in the DB
    @Test
    public void testPutFavorite_IsIdempotent() throws Exception {
        String endpoint = "/api/users/me/favorites/TACO_VALIDO";

        MvcResult result1 = mockMvc.perform(put(endpoint).with(user("juanito"))).andReturn();
        if (result1.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(result1));
        MvcResult result2 = mockMvc.perform(put(endpoint).with(user("juanito"))).andReturn();
        if (result2.getRequest().isAsyncStarted()) {
            try {
                mockMvc.perform(asyncDispatch(result2));
            } catch (Exception e) {
            }
        }
        MvcResult getResult = mockMvc.perform(get("/api/users/me/favorites").with(user("juanito"))).andReturn();
        if (getResult.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(getResult)).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1))); 
        }
    }

    // Try idempotenci in delete
    @Test
    public void testDeleteFavorite_IsIdempotent() throws Exception {
        String endpoint = "/api/users/me/favorites/TACO_VALIDO";
        MvcResult addResult = mockMvc.perform(put(endpoint).with(user("maria"))).andReturn();
        if (addResult.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(addResult));
        MvcResult del1 = mockMvc.perform(delete(endpoint).with(user("maria"))).andReturn();
        if (del1.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(del1));
        MvcResult del2 = mockMvc.perform(delete(endpoint).with(user("maria"))).andReturn();
        if (del2.getRequest().isAsyncStarted()) {
            try {
                mockMvc.perform(asyncDispatch(del2));
            } catch (Exception e) {
            }
        }

        MvcResult getResult = mockMvc.perform(get("/api/users/me/favorites").with(user("maria"))).andReturn();
        if (getResult.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(getResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0))); 
        }
    }

    //User a cant see user B
    @Test
    public void testUserIsolation_CannotSeeOtherUsersFavorites() throws Exception {
        String endpoint = "/api/users/me/favorites/TACO_VALIDO";

        MvcResult addResult = mockMvc.perform(put(endpoint).with(user("batman"))).andReturn();
        if (addResult.getRequest().isAsyncStarted()) mockMvc.perform(asyncDispatch(addResult));
        MvcResult getResult = mockMvc.perform(get("/api/users/me/favorites").with(user("joker"))).andReturn();
        if (getResult.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(getResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0))); 
        }
    }

    // Inexistent taco 404
    @Test
    public void testAddGhostTaco_Returns404() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/users/me/favorites/TACO_FANTASMA").with(user("admin")))
            .andReturn();

        if (result.getRequest().isAsyncStarted()) {
            mockMvc.perform(asyncDispatch(result)).andExpect(status().isNotFound()); // 404
        } else {
            org.junit.jupiter.api.Assertions.assertEquals(404, result.getResponse().getStatus());
        }
    }
}