package tacos;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final ReactiveMongoTemplate mongoTemplate;

    public InventoryService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Void> reserveInventory(TacoOrder order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return Mono.empty();
        }
        Map<String, Integer> requiredStock = new HashMap<>();
        for (OrderItem item : order.getItems()) {
            int qty = item.getQuantity();
            if (item.getTaco() != null && item.getTaco().getIngredients() != null) {
                for (Ingredient ing : item.getTaco().getIngredients()) {
                    requiredStock.put(ing.getId(), requiredStock.getOrDefault(ing.getId(), 0) + qty);
                }
            }
        }
        List<String> sortedIngredientIds = requiredStock.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> successfullyReserved = new ArrayList<>();
        return Flux.fromIterable(sortedIngredientIds)
            .concatMap(ingId -> {
                int needed = requiredStock.get(ingId);
                Query query = new Query(Criteria.where("id").is(ingId).and("stockOnHand").gte(needed));
                Update update = new Update().inc("stockOnHand", -needed);

                return mongoTemplate.updateFirst(query, update, Ingredient.class)
                    .flatMap(result -> {
                        if (result.getModifiedCount() == 0) {
                            return Mono.error(new IllegalStateException("INSUFFICIENT_STOCK: No hay suficiente stock para el ingrediente " + ingId));
                        }
                        successfullyReserved.add(new AbstractMap.SimpleEntry<>(ingId, needed));
                        return Mono.empty();
                    });
            })
            .then()
            .onErrorResume(error -> {
                if (error.getMessage() != null && error.getMessage().contains("INSUFFICIENT_STOCK")) {
                    return rollbackInventory(successfullyReserved)
                            .then(Mono.error(new IllegalArgumentException(error.getMessage())));
                }
                return Mono.error(error);
            });
    }
    public Mono<Void> releaseInventory(TacoOrder order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return Mono.empty();
        }

        Map<String, Integer> stockToRelease = new HashMap<>();
        for (OrderItem item : order.getItems()) {
            int qty = item.getQuantity();
            if (item.getTaco() != null && item.getTaco().getIngredients() != null) {
                for (Ingredient ing : item.getTaco().getIngredients()) {
                    stockToRelease.put(ing.getId(), stockToRelease.getOrDefault(ing.getId(), 0) + qty);
                }
            }
        }

        return Flux.fromIterable(stockToRelease.entrySet())
            .concatMap(entry -> {
                Query query = new Query(Criteria.where("id").is(entry.getKey()));
                Update update = new Update().inc("stockOnHand", entry.getValue());
                return mongoTemplate.updateFirst(query, update, Ingredient.class);
            })
            .then();
    }

    private Mono<Void> rollbackInventory(List<Map.Entry<String, Integer>> reservedList) {
        return Flux.fromIterable(reservedList)
            .concatMap(entry -> {
                Query query = new Query(Criteria.where("id").is(entry.getKey()));
                Update update = new Update().inc("stockOnHand", entry.getValue());
                return mongoTemplate.updateFirst(query, update, Ingredient.class);
            })
            .then();
    }
}