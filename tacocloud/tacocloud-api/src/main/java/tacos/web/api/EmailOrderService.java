package tacos.web.api;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.OrderItem;
import tacos.PaymentMethod;
import tacos.Taco;
import tacos.User;
import tacos.data.IngredientRepository;
import tacos.data.PaymentMethodRepository;
import tacos.data.UserRepository;

import reactor.core.publisher.Flux;

@Service
public class EmailOrderService {

  private UserRepository userRepo;
  private IngredientRepository ingredientRepo;
  private PaymentMethodRepository paymentMethodRepo;

  public EmailOrderService(UserRepository userRepo, IngredientRepository ingredientRepo,
      PaymentMethodRepository paymentMethodRepo) {
    this.userRepo = userRepo;
    this.ingredientRepo = ingredientRepo;
    this.paymentMethodRepo = paymentMethodRepo;
  }

  public Mono<TacoOrder> convertEmailOrderToDomainOrder(Mono<EmailOrder> emailOrder) {
      return emailOrder.flatMap(eOrder -> {

        Mono<User> userMono = userRepo.findByEmail(eOrder.getEmail())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));

        return userMono.flatMap(user -> {
          Mono<PaymentMethod> paymentMono = paymentMethodRepo.findByUserId(user.getId())
              .switchIfEmpty(Mono.error(new IllegalArgumentException("Payment method not found")));
              
          return paymentMono.flatMap(paymentMethod -> {
            TacoOrder order = new TacoOrder();

            order.setUser(user);
            order.setDeliveryName(user.getFullname());
            order.setDeliveryStreet(user.getStreet());
            order.setDeliveryCity(user.getCity());
            order.setDeliveryState(user.getState());
            order.setDeliveryZip(user.getZip());
            order.setPlacedAt(new Date());

            return Flux.fromIterable(eOrder.getTacos()).concatMap(emailTaco -> {
              return Flux.fromIterable(emailTaco.getIngredients()).concatMap(ingredientId -> 
                ingredientRepo.findById(ingredientId)
                  .switchIfEmpty(Mono.error(new IllegalArgumentException("ID ingredient unknown")))
              ).collectList().map(ingredients -> {
                    Taco taco = new Taco();
                    taco.setName(emailTaco.getName());
                    taco.setIngredients(ingredients);
                    return taco;
                  });
            }).collectList().map(tacosList -> {
              List<OrderItem> items = tacosList.stream().map(taco -> {
                OrderItem item = new OrderItem();
                item.setTaco(taco);
                item.setQuantity(1); 
                return item;
              }).collect(Collectors.toList());

              order.setItems(items);
              return order;
            });

          });
        });
      });
  }
}