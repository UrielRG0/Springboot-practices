package tacos.validation;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tacos.Taco;

@Component
public class IngredientCountRule implements TacoValidationRule {
    @Override
    public Flux<ValidationViolation> validate(Taco taco) {
        if (taco.getIngredients() == null) {
            return Flux.just(new ValidationViolation("INSUFFICIENT_INGREDIENTS", "El taco debe tener al menos 2 ingredientes."));
        }
        int count = taco.getIngredients().size();
        if (count < 2 || count > 12) {
            return Flux.just(new ValidationViolation("INVALID_INGREDIENT_COUNT", "El taco debe tener entre 2 y 12 ingredientes (tiene " + count + ")."));
        }
        return Flux.empty();
    }
}