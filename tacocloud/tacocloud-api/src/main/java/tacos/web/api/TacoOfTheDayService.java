package tacos.web.api;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tacos.data.TacoRepository;
import tacos.web.api.TacoOfTheDayResponse;
import tacos.Taco; 

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;

@Service
public class TacoOfTheDayService {

    private final TacoRepository tacoRepository;
    private final Clock clock;

    public TacoOfTheDayService(TacoRepository tacoRepository, Clock clock) {
        this.tacoRepository = tacoRepository;
        this.clock = clock;
    }

    public Mono<TacoOfTheDayResponse> getTacoOfTheDay() {
        LocalDate today = LocalDate.now(clock);

        return tacoRepository.findAll()
            .filter(taco -> taco.getIngredients() != null && !taco.getIngredients().isEmpty())
            .filter(taco -> taco.getIngredients().stream().allMatch(ing -> ing.getStockOnHand() > 0))
            .sort(Comparator.comparing(Taco::getId))
            .collectList()
            .flatMap(candidates -> {
                if (candidates.isEmpty()) {
                    return Mono.empty(); 
                }

                long epochDay = today.toEpochDay();
                int index = (int) Math.abs(epochDay % candidates.size());

                Taco chosen = candidates.get(index);
                String reason = "Selected " + today + " cuz it's the Taco of the Day!";

                return Mono.just(new TacoOfTheDayResponse(chosen, today, reason));
            });
    }
}