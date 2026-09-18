package tacos.validation;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tacos.Taco;
import tacos.Ingredient;

@Component
public class DuplicateIngredientRule implements TacoValidationRule {
    @Override
    public Flux<ValidationViolation> validate(Taco taco) {
        if (taco.getIngredients() == null) return Flux.empty();

        long uniqueCount = taco.getIngredients().stream()
                .map(Ingredient::getId)
                .distinct()
                .count();

        if (uniqueCount < taco.getIngredients().size()) {
            return Flux.just(new ValidationViolation("DUPLICATE_INGREDIENTS", "the taco contains duplicate ingredients."));
        }
        return Flux.empty();
    }
}