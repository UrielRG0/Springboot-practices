package tacos.validation;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tacos.Taco;
import tacos.Ingredient;

@Component
public class IngredientAvailabilityRule implements TacoValidationRule {
    @Override
    public Flux<ValidationViolation> validate(Taco taco) {
        if (taco.getIngredients() == null) return Flux.empty();

        // Como stockOnHand es un int primitivo, evaluamos directo sin el `!= null`
        boolean hasUnavailable = taco.getIngredients().stream()
                .anyMatch(ing -> ing.getStockOnHand() <= 0);

        if (hasUnavailable) {
            return Flux.just(new ValidationViolation("INGREDIENT_OUT_OF_STOCK", "the design contains ingredients that are out of stock."));
        }
        return Flux.empty();
    }
}