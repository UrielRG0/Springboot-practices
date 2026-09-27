package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.Ingredient;
import tacos.InventoryService;
import tacos.OrderItem;
import tacos.Taco;
import tacos.TacoOrder;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = InventoryServiceTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
public class InventoryServiceTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void cleanUp() {
        mongoTemplate.remove(Ingredient.class).all().block();
    }

    @Test
    public void testConcurrentReservation_OnlyOneSucceeds() {
        Ingredient cheese = new Ingredient("CHEESE", "Queso Cheddar", Ingredient.Type.CHEESE);
        cheese.setStockOnHand(1);
        mongoTemplate.save(cheese).block();

        Taco taco = new Taco();
        taco.setIngredients(Collections.singletonList(cheese));

        OrderItem item = new OrderItem();
        item.setTaco(taco);
        item.setQuantity(1);

        TacoOrder order1 = new TacoOrder();
        order1.setItems(Collections.singletonList(item));

        TacoOrder order2 = new TacoOrder();
        order2.setItems(Collections.singletonList(item));
        Mono<Boolean> res1 = inventoryService.reserveInventory(order1).thenReturn(true).onErrorReturn(false);
        Mono<Boolean> res2 = inventoryService.reserveInventory(order2).thenReturn(true).onErrorReturn(false);

        StepVerifier.create(Flux.merge(res1, res2).collectList())
            .assertNext(results -> {
                long successCount = results.stream().filter(r -> r).count();
                assertEquals(1, successCount);
            })
            .verifyComplete();
    }

    @Test
    public void testPartialCompensation_RollsBackOnFailure() {
        Ingredient ing1 = new Ingredient("ING1", "Carne", Ingredient.Type.PROTEIN);
        ing1.setStockOnHand(5);
        mongoTemplate.save(ing1).block();

        Ingredient ing2 = new Ingredient("ING2", "Aguacate", Ingredient.Type.SAUCE);
        ing2.setStockOnHand(0);
        mongoTemplate.save(ing2).block();

        Taco taco = new Taco();
        taco.setIngredients(Arrays.asList(ing1, ing2));

        OrderItem item = new OrderItem();
        item.setTaco(taco);
        item.setQuantity(2); 

        TacoOrder order = new TacoOrder();
        order.setItems(Collections.singletonList(item));
        StepVerifier.create(inventoryService.reserveInventory(order))
            .expectErrorSatisfies(error -> {
                assertTrue(error.getMessage().contains("INSUFFICIENT_STOCK"));
            })
            .verify();

        StepVerifier.create(mongoTemplate.findById("ING1", Ingredient.class))
            .assertNext(ing -> {
                assertEquals(5, ing.getStockOnHand());
            })
            .verifyComplete();
    }

    @Test
    public void testReleaseInventory_RestoresStock() {
        Ingredient ing = new Ingredient("BEEF", "Carne Asada", Ingredient.Type.PROTEIN);
        ing.setStockOnHand(10);
        mongoTemplate.save(ing).block();

        Taco taco = new Taco();
        taco.setIngredients(Collections.singletonList(ing));

        OrderItem item = new OrderItem();
        item.setTaco(taco);
        item.setQuantity(3);

        TacoOrder order = new TacoOrder();
        order.setItems(Collections.singletonList(item));

        StepVerifier.create(inventoryService.releaseInventory(order)).verifyComplete();

        StepVerifier.create(mongoTemplate.findById("BEEF", Ingredient.class))
            .assertNext(i -> {
                assertEquals(13, i.getStockOnHand());
            }).verifyComplete();
    }
}