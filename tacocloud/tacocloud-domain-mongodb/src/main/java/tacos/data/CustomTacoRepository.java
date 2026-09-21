package tacos.data;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.Taco;
import tacos.TacoSearchCriteria;

public interface CustomTacoRepository {
    Flux<Taco> searchTacos(TacoSearchCriteria criteria);
    Mono<Long> countTacos(TacoSearchCriteria criteria);
}