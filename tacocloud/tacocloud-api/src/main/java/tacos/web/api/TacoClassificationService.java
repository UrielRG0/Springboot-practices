package tacos.web.api;

import org.springframework.stereotype.Service;
import tacos.data.TacoRepository;
import tacos.Taco;
import tacos.Ingredient;
import tacos.DietaryTag;
import tacos.Allergen;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Service
public class TacoClassificationService {

    private final TacoRepository tacoRepository;

    public TacoClassificationService(TacoRepository tacoRepository) {
        this.tacoRepository = tacoRepository;
    }

    public Mono<TacoClassification> classifyTaco(String tacoId) {
        return tacoRepository.findById(tacoId)
            .map(this::calculateClassification);
    }

    public TacoClassification calculateClassification(Taco taco) {
        if (taco.getIngredients() == null || taco.getIngredients().isEmpty()) {
            return new TacoClassification(Collections.emptySet(), Collections.emptySet(), 0);
        }

        Set<DietaryTag> dietaryTags = EnumSet.allOf(DietaryTag.class);
        Set<Allergen> allergens = new HashSet<>();
        int maxSpiceLevel = 0;

        for (Ingredient ing : taco.getIngredients()) {
            if (ing.getDietaryTags() != null) {
                dietaryTags.retainAll(ing.getDietaryTags());
            } else {
                dietaryTags.clear();
            }

            if (ing.getAllergens() != null) {
                allergens.addAll(ing.getAllergens());
            }

            if (ing.getSpiceLevel() > maxSpiceLevel) {
                maxSpiceLevel = ing.getSpiceLevel();
            }
        }

        return new TacoClassification(dietaryTags, allergens, maxSpiceLevel);
    }
    public static class TacoClassification {
        private Set<DietaryTag> dietaryTags;
        private Set<Allergen> allergens;
        private int spiceLevel;
        private String academicDisclaimer = "Caution: This classification is based on the ingredients provided and may not account for cross-contamination or other factors. Always check with the restaurant for specific dietary needs.";

        public TacoClassification(Set<DietaryTag> dietaryTags, Set<Allergen> allergens, int spiceLevel) {
            this.dietaryTags = dietaryTags;
            this.allergens = allergens;
            this.spiceLevel = spiceLevel;
        }

        public Set<DietaryTag> getDietaryTags() { return dietaryTags; }
        public Set<Allergen> getAllergenSet() { return allergens; }
        public int getSpiceLevel() { return spiceLevel; }
        public String getAcademicDisclaimer() { return academicDisclaimer; }
    }
}