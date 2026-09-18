package tacos.web.api;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.OrderItem;
import tacos.Ingredient;
import tacos.data.IngredientRepository;
import tacos.pricing.CouponService; 

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderPricingService {

    private final IngredientRepository ingredientRepo;
    private final CouponService couponService;
    public OrderPricingService(IngredientRepository ingredientRepo, CouponService couponService) {
        this.ingredientRepo = ingredientRepo;
        this.couponService = couponService;
    }

    public Mono<TacoOrder> calculatePrices(TacoOrder order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return Mono.error(new IllegalArgumentException("The order cant be empty. It must contain at least one taco."));
        }

        return Flux.fromIterable(order.getItems())
            .flatMap(item -> {
                if (item.getQuantity() <= 0 || item.getQuantity() > 10) {
                    return Mono.error(new IllegalArgumentException("Error: The quantity per taco must be between 1 and 10."));
                }

                List<String> ingredientIds = item.getTaco().getIngredients().stream()
                        .map(Ingredient::getId)
                        .collect(Collectors.toList());

                return ingredientRepo.findAllById(ingredientIds)
                    .map(Ingredient::getUnitPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add) 
                    .map(tacoPrice -> {
                        BigDecimal finalTacoPrice = tacoPrice.setScale(2, RoundingMode.HALF_UP);
                        item.setUnitPriceAtPurchase(finalTacoPrice); 
                        
                        BigDecimal subtotal = finalTacoPrice.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);
                        item.setSubtotal(subtotal);
                        return item;
                    });
            })
            .collectList()
            .map(processedItems -> {
                order.setItems(processedItems);
                BigDecimal subtotal = processedItems.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                BigDecimal discount = couponService.calculateDiscount(order.getDiscountCode(), subtotal);
                order.setDiscountAmount(discount);
                BigDecimal total = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
                order.setTotal(total);
                
                return order;
            });
    }
}