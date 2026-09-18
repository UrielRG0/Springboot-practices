package tacos.validation;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tacos.Taco;
import tacos.DietaryTag;
import tacos.Ingredient;

@Component
public class VeganConflictRule implements TacoValidationRule {
    @Override
    public Flux<ValidationViolation> validate(Taco taco) {
        if (taco.getIngredients() == null) return Flux.empty();

        boolean hasExtremeSpice = taco.getIngredients().stream().anyMatch(i -> i.getSpiceLevel() >= 4);
        boolean isDeclaredVegan = taco.getIngredients().stream().allMatch(i -> i.getDietaryTags() != null && i.getDietaryTags().contains(DietaryTag.VEGAN));

        if (hasExtremeSpice && isDeclaredVegan) {
            return Flux.just(new ValidationViolation("VEGAN_SPICE_CONFLICT", "The taco is declared vegan but contains ingredients with extreme spice levels (4 or higher)."));
        }
        
        return Flux.empty();
    }
}