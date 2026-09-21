package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import tacos.TacoRankingDTO;
import tacos.TacoRating;
import tacos.User;
import tacos.data.TacoRatingRepository;
import tacos.data.TacoRepository;

import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
public class TacoRatingApiController {

    private final TacoRatingRepository ratingRepo;
    private final TacoRepository tacoRepo;

    public TacoRatingApiController(TacoRatingRepository ratingRepo, TacoRepository tacoRepo) {
        this.ratingRepo = ratingRepo;
        this.tacoRepo = tacoRepo;
    }


    @Data
    public static class ScoreRequest {
        private int score;
    }
    @PutMapping("/api/tacos/{id}/rating")
    public Mono<ResponseEntity<Void>> rateTaco(@PathVariable("id") String tacoId,@RequestBody ScoreRequest request,@AuthenticationPrincipal User user) {
        
        if (request.score < 1 || request.score > 5) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return tacoRepo.findById(tacoId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "El taco no existe")))
            .flatMap(taco -> 
                ratingRepo.findByUserIdAndTacoId(user.getId(), tacoId)
                    .flatMap(existingRating -> {
                        existingRating.setScore(request.score);
                        existingRating.setRatedAt(new Date());
                        return ratingRepo.save(existingRating);
                    })
                    .switchIfEmpty(ratingRepo.save(new TacoRating(null, user.getId(), tacoId, request.score, new Date()))
                    )
            )
            .map(saved -> ResponseEntity.ok().<Void>build());
    }

    @GetMapping("/api/tacos/top")
    public Flux<TacoRankingDTO> getTopTacos(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "3") int minVotes) {
        
        return ratingRepo.getTopTacos(limit, minVotes);
    }
}