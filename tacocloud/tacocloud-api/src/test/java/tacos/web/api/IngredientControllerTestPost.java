package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import tacos.Ingredient;
import tacos.Ingredient.Type;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
@AutoConfigureWebTestClient
public class IngredientControllerTestPost {

    @SpringBootApplication(scanBasePackages = "tacos")
    @EnableReactiveMongoRepositories(basePackages = "tacos.data")
    static class TestApplication {
    }

    @Autowired //webtest client
    private WebTestClient testClient;

    //200 y 201 OK
    @Test
    public void testPostIngredient201202(){
        Ingredient newIngredient = new Ingredient("CHOR", "Chorizo", Type.PROTEIN);
        String locationUrl = testClient.post().uri("/api/ingredients").contentType(MediaType.APPLICATION_JSON).bodyValue(newIngredient).exchange().expectStatus().isCreated()
            .expectHeader().exists("Location").returnResult(Ingredient.class).getResponseHeaders().getLocation().toString();

        testClient.get().uri(locationUrl).exchange().expectStatus().isOk().expectBody().jsonPath("$.id").isEqualTo("CHOR");
    }

    //400
    @Test
    public void testPostIngredient400(){
        Ingredient invalidIngredient = new Ingredient("", "", null);

        testClient.post().uri("/api/ingredients").contentType(MediaType.APPLICATION_JSON).bodyValue(invalidIngredient).exchange().expectStatus().isBadRequest();
    }
}