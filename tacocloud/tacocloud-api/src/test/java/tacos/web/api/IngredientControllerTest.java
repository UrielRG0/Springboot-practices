package tacos.web.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;
import reactor.test.StepVerifier;

public class IngredientControllerTest {

    //Check if the ID exist
    @Test
    public void testUpdateIngredient_400_BadRequest(){
        IngredientRepository repo=Mockito.mock(IngredientRepository.class);
        IngredientController controller=new IngredientController(repo);
        WebTestClient testClient=WebTestClient.bindToController(controller).build();

        Ingredient ingredienteMalo = new Ingredient("CARN", "Carnitas", Type.PROTEIN);

        testClient.put().uri("/api/ingredients/FLTO").contentType(MediaType.APPLICATION_JSON).bodyValue(ingredienteMalo).exchange().expectStatus().isBadRequest();
        verify(repo, never()).save(any()); 
    }

    //Check if the ID exist
    @Test
    public void testUpdateIngredient_404_NotFound(){
        IngredientRepository repo = Mockito.mock(IngredientRepository.class);
        when(repo.findById("FLTO")).thenReturn(Mono.empty()); 
        
        IngredientController controller=new IngredientController(repo);
        WebTestClient testClient= WebTestClient.bindToController(controller).build();

        Ingredient ingredienteInexistente=new Ingredient("FLTO", "Flour Tortilla", Type.WRAP);

        testClient.put().uri("/api/ingredients/FLTO").contentType(MediaType.APPLICATION_JSON).bodyValue(ingredienteInexistente).exchange().expectStatus().isNotFound();
        verify(repo, never()).save(any());
    }

    //200 ok
    @Test
    public void testUpdateIngredient_200_Success(){
        IngredientRepository repo = Mockito.mock(IngredientRepository.class);
        Ingredient ingredienteActualizado = new Ingredient("FLTO", "Salsa Verde", Type.SAUCE);
        
        when(repo.findById("FLTO")).thenReturn(Mono.just(ingredienteActualizado));
        when(repo.save(any())).thenReturn(Mono.just(ingredienteActualizado));
        
        IngredientController controller = new IngredientController(repo);
        WebTestClient testClient = WebTestClient.bindToController(controller).build();

        testClient.put().uri("/api/ingredients/FLTO").contentType(MediaType.APPLICATION_JSON).bodyValue(ingredienteActualizado).exchange().expectStatus().isOk();
        verify(repo, Mockito.times(1)).save(any());
    }

    //check if the ID exist with StepVerifier
    @Test
    public void testUpdateIngredient_StepVerifier(){
        IngredientRepository repo = Mockito.mock(IngredientRepository.class);
        Ingredient ingredienteActualizado = new Ingredient("FLTO", "Salsa Verde", Type.SAUCE);
        when(repo.findById("FLTO")).thenReturn(Mono.just(ingredienteActualizado));
        when(repo.save(any())).thenReturn(Mono.just(ingredienteActualizado));
        
        IngredientController controller = new IngredientController(repo);
        Mono<ResponseEntity<Ingredient>> resultado = controller.updateIngredient("FLTO", ingredienteActualizado);
        
        StepVerifier.create(resultado).expectNextMatches(response -> response.getStatusCode().is2xxSuccessful()).verifyComplete();
        verify(repo, Mockito.times(1)).save(any());
    }
}