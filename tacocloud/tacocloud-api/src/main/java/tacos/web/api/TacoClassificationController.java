package tacos.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/tacos", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class TacoClassificationController {

    private final TacoClassificationService classificationService;

    public TacoClassificationController(TacoClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @GetMapping("/{id}/classification")
    public Mono<ResponseEntity<TacoClassificationService.TacoClassification>> getTacoClassification(@PathVariable("id") String id) {
        return classificationService.classifyTaco(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}