package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.autoconfigure.SpringBootApplication; 
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient 
public class IngredientControllerTestDeleteMongoDB{

    @SpringBootApplication(scanBasePackages="tacos")
    @EnableReactiveMongoRepositories(basePackages="tacos.data") 
    static class TestApplication{
    }

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private IngredientRepository repo; 

    @Test
    public void testDeleteIntegration_MongoConfirmarDesaparicion(){
        Ingredient ingredienteTemp=new Ingredient("BYEB", "InDe", Type.VEGGIES); 
        StepVerifier.create(repo.save(ingredienteTemp)).expectNextCount(1).verifyComplete(); 

        testClient.delete().uri("/api/ingredients/BYEB").exchange().expectStatus().isNoContent(); 

        Mono<Ingredient> busquedaFinal=repo.findById("BYEB");
        StepVerifier.create(busquedaFinal).verifyComplete(); 
    }
}