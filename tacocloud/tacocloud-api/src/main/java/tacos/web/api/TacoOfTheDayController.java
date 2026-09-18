package tacos.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tacos.web.api.TacoOfTheDayService;

@RestController
@RequestMapping(path = "/api/tacos", produces = "application/json")
public class TacoOfTheDayController {

    private final TacoOfTheDayService tacoOfTheDayService;

    public TacoOfTheDayController(TacoOfTheDayService tacoOfTheDayService) {
        this.tacoOfTheDayService = tacoOfTheDayService;
    }

    @GetMapping("/today")
    public Mono<ResponseEntity<TacoOfTheDayResponse>> getTacoOfTheDay() {
        return tacoOfTheDayService.getTacoOfTheDay()
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.<TacoOfTheDayResponse>notFound().build()); // Tipado explícito para evitar conflictos
    }
}