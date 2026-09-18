package tacos.validation;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.Taco;

import java.util.List;

@Service
public class TacoValidatorService {

    private final List<TacoValidationRule> rules;

    // Spring inyecta automáticamente TODAS las reglas que creamos
    public TacoValidatorService(List<TacoValidationRule> rules) {
        this.rules = rules;
    }

    public Flux<ValidationViolation> validate(Taco taco) {
        return Flux.fromIterable(rules)
                .flatMap(rule -> rule.validate(taco));
    }

    public Mono<Void> validateAndThrow(Taco taco) {
        return validate(taco)
                .collectList()
                .flatMap(violations -> {
                    if (!violations.isEmpty()) {
                        StringBuilder sb = new StringBuilder("Taco inválido:");
                        for (ValidationViolation v : violations) {
                            sb.append(" [").append(v.getCode()).append("] ").append(v.getMessage());
                        }
                        return Mono.error(new IllegalArgumentException(sb.toString()));
                    }
                    return Mono.empty();
                });
    }
}