package tacos.web.api;

import org.junit.jupiter.api.Test;
import tacos.data.IngredientRepository;
import org.mockito.Mockito;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import reactor.core.publisher.Mono;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyString;



public class IngredientControllerTestDelete {
    //204 ok
    @Test
    public void testDeleteIngredient(){
        IngredientRepository repo = Mockito.mock(IngredientRepository.class);
        Ingredient ingredient=new Ingredient("FLTO", "Flour Tortilla", Type.WRAP);

        when(repo.findById("FLTO")).thenReturn(Mono.just(ingredient));
        when(repo.deleteById("FLTO")).thenReturn(Mono.empty());

        IngredientController controller=new IngredientController(repo);
        WebTestClient testClient=WebTestClient.bindToController(controller).build();

        testClient.delete().uri("/api/ingredients/FLTO").exchange().expectStatus().isNoContent();

        verify(repo, Mockito.times(1)).deleteById("FLTO");
    }

    //404 not found
    @Test
    public void testDeleteIngredientNotFound(){
        IngredientRepository repo=Mockito.mock(IngredientRepository.class);

        when(repo.findById("FLTO")).thenReturn(Mono.empty());

        IngredientController controller=new IngredientController(repo);
        WebTestClient testClient=WebTestClient.bindToController(controller).build();

        testClient.delete().uri("/api/ingredient/FLTO").exchange().expectStatus().isNotFound();
        verify(repo, never()).deleteById(anyString());
    }



}
