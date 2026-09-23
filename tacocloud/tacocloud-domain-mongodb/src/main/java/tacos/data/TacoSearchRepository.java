package tacos.data;

import reactor.core.publisher.Flux;
import tacos.Taco;
import tacos.DietaryTag;
import tacos.Allergen;

public interface TacoSearchRepository {
    Flux<Taco> searchTacos(String name, String ingredientId, DietaryTag diet, Allergen excludeAllergen, Integer spice, int page, int size, String sortBy, String sortDir);
}