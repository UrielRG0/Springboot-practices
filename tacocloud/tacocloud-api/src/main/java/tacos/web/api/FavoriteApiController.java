package tacos.web.api;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.Favorite;
import tacos.User;
import tacos.data.FavoriteRepository;
import tacos.data.TacoRepository;

import java.util.Date;

@RestController
@RequestMapping(path = "/api/users/me/favorites", produces = "application/json")
@CrossOrigin(origins = "*")
public class FavoriteApiController {

    private final FavoriteRepository favoriteRepo;
    private final TacoRepository tacoRepo;

    public FavoriteApiController(FavoriteRepository favoriteRepo, TacoRepository tacoRepo) {
        this.favoriteRepo = favoriteRepo;
        this.tacoRepo = tacoRepo;
    }

    @PutMapping("/{tacoId}")
    public Mono<ResponseEntity<Void>> addFavorite(
            @PathVariable String tacoId,
            @AuthenticationPrincipal User user) {

        return tacoRepo.findById(tacoId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "El taco no existe")))
            .flatMap(taco -> {
                Favorite fav = new Favorite(null, user.getId(), tacoId, new Date());
                
                return favoriteRepo.save(fav)
                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED).<Void>build()).onErrorResume(DuplicateKeyException.class, e -> 
                        Mono.just(ResponseEntity.ok().<Void>build())
                    );
            });
    }

    // DELETE: Quitar favorito
    @DeleteMapping("/{tacoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeFavorite(@PathVariable String tacoId,@AuthenticationPrincipal User user) {

        return favoriteRepo.deleteByUserIdAndTacoId(user.getId(), tacoId);
    }

    @GetMapping
    public Flux<Favorite> getMyFavorites(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return favoriteRepo.findByUserId(user.getId(), pageRequest);
    }
}