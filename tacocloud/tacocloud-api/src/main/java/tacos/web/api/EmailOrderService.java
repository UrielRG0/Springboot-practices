package tacos.web.api;

//import java.util.ArrayList;
import java.util.Date;
//import java.util.List;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
//import tacos.Ingredient;
import tacos.TacoOrder;
import tacos.PaymentMethod;
import tacos.Taco;
import tacos.User;
import tacos.data.IngredientRepository;
import tacos.data.PaymentMethodRepository;
import tacos.data.UserRepository;
//import tacos.web.api.EmailOrder.EmailTaco;

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
    // TODO: Probably should handle unhappy case where email address doesn't match a given user or
    //       where the user doesn't have at least one payment method.

      return emailOrder.flatMap(eOrder -> {

        Mono<User> userMono = userRepo.findByEmail(eOrder.getEmail()).switchIfEmpty(Mono.error(new IllegalArgumentException("User not founded")));

        return userMono.flatMap(user ->{
          Mono<PaymentMethod> paymentMono = paymentMethodRepo.findByUserId(user.getId()).switchIfEmpty(Mono.error(new IllegalArgumentException("Payment method not founded")));
          return paymentMono.flatMap(paymentMethod -> {
            TacoOrder order = new TacoOrder();

            order.setUser(user);
            order.setCcNumber(paymentMethod.getCcNumber());
            order.setCcCVV(paymentMethod.getCcCVV());
            order.setCcExpiration(paymentMethod.getCcExpiration());
            order.setDeliveryName(user.getFullname());
            order.setDeliveryStreet(user.getStreet());
            order.setDeliveryCity(user.getCity());
            order.setDeliveryState(user.getState());
            order.setDeliveryZip(user.getZip());
            order.setPlacedAt(new Date());

            return Flux.fromIterable(eOrder.getTacos()).concatMap(emailTaco -> {
              return Flux.fromIterable(emailTaco.getIngredients()).concatMap(ingredientId -> ingredientRepo.findById(ingredientId)
                  .switchIfEmpty(Mono.error(new IllegalArgumentException("ID ingredient unknown")))).collectList().map(ingredients->{
                    Taco taco = new Taco();
                    taco.setName(emailTaco.getName());
                    taco.setIngredients(ingredients);
                    return taco;
                  });
            }).collectList().map(tacosList->{
              order.setTacos(tacosList);
              return order;
            });

          });
        });


      });


      //Mono<PaymentMethod> paymentMono = userMono.flatMap(user -> {
      //  return paymentMethodRepo.findByUserId(user.getId());
      //});
      



    }

}
