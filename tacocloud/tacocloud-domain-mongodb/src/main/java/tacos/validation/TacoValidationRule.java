package tacos.validation;

import reactor.core.publisher.Flux;
import tacos.Taco;

public interface TacoValidationRule {
    Flux<ValidationViolation> validate(Taco taco);
}