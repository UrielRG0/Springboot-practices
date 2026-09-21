package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reactor.core.publisher.Mono;
import tacos.Taco;
import tacos.TacoSearchCriteria;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path = "/api/tacos", produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class TacoController {

  private final TacoRepository tacoRepo;
  public TacoController(TacoRepository tacoRepo) {
    this.tacoRepo = tacoRepo;
  }

  @GetMapping
  public Mono<ResponseEntity<Map<String, Object>>> searchTacos(@Valid @ModelAttribute TacoSearchCriteria criteria) {
      
      return tacoRepo.searchTacos(criteria).collectList()
          .zipWith(tacoRepo.countTacos(criteria))
          .map(tuple -> {
              List<Taco> tacos = tuple.getT1();
              Long total = tuple.getT2();
              
              Map<String, Object> response = new HashMap<>();
              response.put("content", tacos);
              response.put("page", criteria.getPage());
              response.put("size", criteria.getSize());
              response.put("totalElements", total);
              int pageSize = Math.max(1, criteria.getSize());
              response.put("totalPages", (int) Math.ceil((double) total / pageSize));
              
              return ResponseEntity.ok(response);
          });
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