package tacos.data;

import reactor.core.publisher.Flux;
import tacos.TacoRankingDTO;

public interface CustomTacoRatingRepository {
    Flux<TacoRankingDTO> getTopTacos(int limit, int minVotes);
}