package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import tacos.Ingredient;
import tacos.OrderItem;
import tacos.Taco;
import tacos.TacoOrder;
import tacos.data.IngredientRepository;
import tacos.pricing.CouponService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderPricingTest {

    private IngredientRepository ingredientRepo;
    private CouponService couponService;
    private OrderPricingService pricingService;

    @BeforeEach
    public void setup() {
        ingredientRepo = mock(IngredientRepository.class);
        couponService = mock(CouponService.class);
        pricingService = new OrderPricingService(ingredientRepo, couponService);
    }

    @Test
    public void testCalculatePrices_TwoUnits_DoublesSubtotal_IgnoresClientTotal() {
        Ingredient carnitas = new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN);
        carnitas.setUnitPrice(new BigDecimal("10.50"));

        when(ingredientRepo.findAllById(any(Iterable.class))).thenReturn(Flux.just(carnitas));
        when(couponService.calculateDiscount(any(), any())).thenReturn(BigDecimal.ZERO);

        Taco taco = new Taco();
        taco.setIngredients(Collections.singletonList(carnitas));

        OrderItem item = new OrderItem();
        item.setTaco(taco);
        item.setQuantity(2); 
        
        TacoOrder order = new TacoOrder();
        order.setItems(Arrays.asList(item));
        order.setTotal(new BigDecimal("1.00")); 

        StepVerifier.create(pricingService.calculatePrices(order))
            .assertNext(pricedOrder -> {
                OrderItem processedItem = pricedOrder.getItems().get(0);
                assertEquals(new BigDecimal("10.50").setScale(2, RoundingMode.HALF_UP), processedItem.getUnitPriceAtPurchase());
                assertEquals(new BigDecimal("21.00").setScale(2, RoundingMode.HALF_UP), processedItem.getSubtotal());
                assertEquals(new BigDecimal("21.00").setScale(2, RoundingMode.HALF_UP), pricedOrder.getTotal());
            }).verifyComplete();
    }

    @Test
    public void testCalculatePrices_InvalidQuantity_ThrowsException() {
        Taco taco = new Taco();
        taco.setIngredients(Collections.singletonList(new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN)));

        OrderItem item = new OrderItem();
        item.setTaco(taco);
        item.setQuantity(0);

        TacoOrder order = new TacoOrder();
        order.setItems(Arrays.asList(item));

        StepVerifier.create(pricingService.calculatePrices(order)).expectErrorMessage("Error: The quantity per taco must be between 1 and 10.").verify();
    }
}