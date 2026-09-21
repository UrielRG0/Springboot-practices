package tacos.web.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Taco;
import tacos.data.TacoRepository;
// Se elimina el import de TacoSearchRepository porque ya no existe

public class TacoControllerTest {

  @Test
  public void shouldReturnRecentTacos() {
    Taco[] tacos = {
        testTaco(1L), testTaco(2L),
        testTaco(3L), testTaco(4L),
        testTaco(5L), testTaco(6L),
        testTaco(7L), testTaco(8L),
        testTaco(9L), testTaco(10L),
        testTaco(11L), testTaco(12L) // Mockeamos 12 tacos para la prueba
    };
    Flux<Taco> tacoFlux = Flux.just(tacos);

    TacoRepository tacoRepo = Mockito.mock(TacoRepository.class);
    
    // Ahora mockeamos los métodos custom de búsqueda y conteo en lugar de findAll()
    when(tacoRepo.searchTacos(any())).thenReturn(tacoFlux);
    when(tacoRepo.countTacos(any())).thenReturn(Mono.just(12L));

    // Solo le inyectamos el tacoRepo al controlador
    WebTestClient testClient = WebTestClient.bindToController(
        new TacoController(tacoRepo))
        .build();

    testClient.get().uri("/api/tacos?recent").exchange().expectStatus().isOk().expectBody().jsonPath("$.content").isArray().jsonPath("$.content").isNotEmpty()
        .jsonPath("$.content[0].id").isEqualTo(tacos[0].getId().toString()).jsonPath("$.content[0].name").isEqualTo("Taco 1").jsonPath("$.content[1].id").isEqualTo(tacos[1].getId().toString())
        .jsonPath("$.content[1].name").isEqualTo("Taco 2").jsonPath("$.content[11].id").isEqualTo(tacos[11].getId().toString()).jsonPath("$.content[11].name").isEqualTo("Taco 12")
        .jsonPath("$.content[12]").doesNotExist().jsonPath("$.page").isEqualTo(0).jsonPath("$.totalElements").isEqualTo(12);
  }

  @Test
  public void shouldSaveATaco() {
    TacoRepository tacoRepo = Mockito.mock(TacoRepository.class);
    
    Mono<Taco> unsavedTacoMono = Mono.just(testTaco(null));
    Taco savedTaco = testTaco(null);
    Mono<Taco> savedTacoMono = Mono.just(savedTaco);

    when(tacoRepo.save(any())).thenReturn(savedTacoMono);
    WebTestClient testClient = WebTestClient.bindToController(
        new TacoController(tacoRepo)).build(); 

    testClient.post()
        .uri("/api/tacos")
        .contentType(MediaType.APPLICATION_JSON)
        .body(unsavedTacoMono, Taco.class)
      .exchange()
      .expectStatus().isCreated()
      .expectBody(Taco.class)
        .isEqualTo(savedTaco);
  }

  private Taco testTaco(Long number) {
    Taco taco = new Taco();
    taco.setId(number != null ? number.toString(): "TESTID");
    taco.setName("Taco " + number);
    List<Ingredient> ingredients = new ArrayList<>();
    ingredients.add(
        new Ingredient("INGA", "Ingredient A", Type.WRAP));
    ingredients.add(
        new Ingredient("INGB", "Ingredient B", Type.PROTEIN));
    taco.setIngredients(ingredients);
    return taco;
  }
}