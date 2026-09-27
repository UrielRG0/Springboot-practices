package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.*;
import tacos.data.TacoRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TacoClassificationTest {

    private TacoRepository tacoRepository;
    private TacoClassificationService classificationService;

    @BeforeEach
    public void setUp() {
        tacoRepository = Mockito.mock(TacoRepository.class);
        classificationService = new TacoClassificationService(tacoRepository);
    }

    @Test
    public void testTacoComposition_NonVeganIngredientBreaksVeganStatus() {
        Ingredient tortilla = new Ingredient("TMAC", "Tortilla Maíz", Ingredient.Type.WRAP);
        tortilla.setDietaryTags(new HashSet<>(Arrays.asList(DietaryTag.VEGAN, DietaryTag.GLUTEN_FREE)));
        tortilla.setSpiceLevel(0);
        Ingredient beef = new Ingredient("BEEF", "Carne Asada", Ingredient.Type.PROTEIN);
        beef.setDietaryTags(Collections.emptySet());
        beef.setSpiceLevel(2);

        Taco taco = new Taco();
        taco.setId("taco-1");
        taco.setName("Taco Mixto");
        taco.setIngredients(Arrays.asList(tortilla, beef));

        when(tacoRepository.findById("taco-1")).thenReturn(Mono.just(taco));

        StepVerifier.create(classificationService.classifyTaco("taco-1"))
            .assertNext(classification -> {
                assertTrue(!classification.getDietaryTags().contains(DietaryTag.VEGAN));
                assertEquals(2, classification.getSpiceLevel());
            })
            .verifyComplete();
    }

    @Test
    public void testAllergensUnion_CombinesAllIngredients() {
        Ingredient ing1 = new Ingredient("ING1", "Queso", Ingredient.Type.CHEESE);
        ing1.setAllergens(Collections.singleton(Allergen.DAIRY));
        ing1.setDietaryTags(Collections.singleton(DietaryTag.VEGETARIAN));

        Ingredient ing2 = new Ingredient("ING2", "Salsa de Soya", Ingredient.Type.SAUCE);
        ing2.setAllergens(Collections.singleton(Allergen.SOY));
        ing2.setDietaryTags(Collections.singleton(DietaryTag.VEGAN));

        Taco taco = new Taco();
        taco.setId("taco-2");
        taco.setIngredients(Arrays.asList(ing1, ing2));

        when(tacoRepository.findById("taco-2")).thenReturn(Mono.just(taco));

        StepVerifier.create(classificationService.classifyTaco("taco-2"))
            .assertNext(classification -> {
                assertEquals(2, classification.getAllergenSet().size());
                assertTrue(classification.getAllergenSet().contains(Allergen.DAIRY));
                assertTrue(classification.getAllergenSet().contains(Allergen.SOY));
            })
            .verifyComplete();
    }

    @Test
    public void testSpicePolicy_CalculatesMaxSpiceLevel() {
        Ingredient mild = new Ingredient("MILD", "Pimiento", Ingredient.Type.VEGGIES);
        mild.setSpiceLevel(1);

        Ingredient spicy = new Ingredient("SPICY", "Habanero", Ingredient.Type.VEGGIES);
        spicy.setSpiceLevel(5);

        Taco taco = new Taco();
        taco.setId("taco-3");
        taco.setIngredients(Arrays.asList(mild, spicy));

        when(tacoRepository.findById("taco-3")).thenReturn(Mono.just(taco));

        StepVerifier.create(classificationService.classifyTaco("taco-3"))
            .assertNext(classification -> {
                assertEquals(5, classification.getSpiceLevel());
            })
            .verifyComplete();
    }
}