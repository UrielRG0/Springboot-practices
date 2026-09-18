package tacos.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tacos.Taco;
import tacos.validation.TacoValidatorService;
import tacos.validation.ValidationViolation;

import java.util.List;

@RestController
@RequestMapping(path = "/api/tacos", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class TacoValidationController {

    private final TacoValidatorService validatorService;

    public TacoValidatorService getValidatorService() {
        return validatorService;
    }

    public TacoValidationController(TacoValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<List<ValidationViolation>>> validateTacoDesign(@RequestBody Taco taco) {
        return validatorService.validate(taco)
                .collectList()
                .map(violations -> {
                    if (violations.isEmpty()) {
                        return ResponseEntity.ok(violations); 
                    } else {
                        return ResponseEntity.badRequest().body(violations); 
                    }
                });
    }
}