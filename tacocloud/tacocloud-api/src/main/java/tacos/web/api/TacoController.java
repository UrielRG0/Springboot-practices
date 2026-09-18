package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.Taco;
import tacos.DietaryTag;
import tacos.Allergen;
import tacos.data.TacoRepository;
import tacos.data.TacoSearchRepository;

@RestController
@RequestMapping(path = "/api/tacos", produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class TacoController {

  private final TacoRepository tacoRepo;
  private final TacoSearchRepository tacoSearchRepo; 

  public TacoController(TacoRepository tacoRepo, TacoSearchRepository tacoSearchRepo) {
    this.tacoRepo = tacoRepo;
    this.tacoSearchRepo = tacoSearchRepo;
  }

  @GetMapping(params="recent")
  public Flux<Taco> recentTacos() {
    return tacoRepo.findAll().take(12);
  }

  @GetMapping
  public Flux<Taco> searchTacos(
          @RequestParam(required = false) String name,
          @RequestParam(required = false) String ingredientId,
          @RequestParam(required = false) DietaryTag diet,
          @RequestParam(required = false) Allergen excludeAllergen,
          @RequestParam(required = false) Integer spice,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size,
          @RequestParam(defaultValue = "createdAt,desc") String sort) {

      String sortBy = "createdAt";
      String sortDir = "desc";
      if (sort != null && sort.contains(",")) {
          String[] parts = sort.split(",");
          sortBy = parts[0];
          sortDir = parts[1];
      }

      return tacoSearchRepo.searchTacos(name, ingredientId, diet, excludeAllergen, spice, page, size, sortBy, sortDir);
  }

  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Taco> postTaco(@RequestBody Taco taco) {
    return tacoRepo.save(taco);
  }

  @GetMapping("/{id}")
  public Mono<Taco> tacoById(@PathVariable("id") String id) {
    return tacoRepo.findById(id);
  }

}