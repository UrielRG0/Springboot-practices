package tacos.validation;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tacos.Taco;

@Component
public class BaseRule implements TacoValidationRule {
    @Override
    public Flux<ValidationViolation> validate(Taco taco) {
        if (taco.getName() == null || taco.getName().trim().isEmpty()) {
            return Flux.just(new ValidationViolation("MISSING_BASE", "El taco debe tener un nombre o diseño válido."));
        }
        return Flux.empty();
    }
}