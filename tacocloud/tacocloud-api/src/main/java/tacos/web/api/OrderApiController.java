package tacos.web.api;

//import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.messaging.OrderMessagingService;

@RestController
@RequestMapping(path="/api/orders",
                produces="application/json")
@CrossOrigin(origins="http://localhost:8080")
public class OrderApiController {

  private OrderRepository repo;
  private OrderMessagingService orderMessages;
  private EmailOrderService emailOrderService;

  public OrderApiController(OrderRepository repo,
                            OrderMessagingService orderMessages,
                            EmailOrderService emailOrderService) {
    this.repo = repo;
    this.orderMessages = orderMessages;
    this.emailOrderService = emailOrderService;
  }

  @GetMapping(produces="application/json")
  public Flux<TacoOrder> allOrders() {
    return repo.findAll();
  }

//  @PostMapping(consumes="application/json")
//  @ResponseStatus(HttpStatus.CREATED)
//  public Mono<Order> postOrder(@RequestBody Mono<Order> order) {
//    order.subscribe(orderMessages::sendOrder); // TODO: not ideal...work into reactive flow below
//    return order
//        .flatMap(repo::save);
//  }

  @PostMapping(consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TacoOrder> postOrder(@RequestBody TacoOrder order) {
    orderMessages.sendOrder(order);
    return repo.save(order);
  }

  //@PostMapping(path="fromEmail", consumes="application/json")
  //@ResponseStatus(HttpStatus.CREATED)
  //public Mono<TacoOrder> postOrderFromEmail(@RequestBody Mono<EmailOrder> emailOrder) {
  //  Mono<TacoOrder> order = emailOrderService.convertEmailOrderToDomainOrder(emailOrder);
  //  order.subscribe(orderMessages::sendOrder); // TODO: not ideal...work into reactive flow below
  //  return order
  //      .flatMap(repo::save);
  //}
  @PostMapping(path="/fromEmail", consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TacoOrder> postOrderFromEmail(@RequestBody Mono<EmailOrder> emailOrder){
    return emailOrderService.convertEmailOrderToDomainOrder(emailOrder).flatMap(order -> repo.save(order)).doOnNext(savedOrder ->{
      orderMessages.sendOrder(savedOrder);
    });
    
  }


  //@PutMapping(path="/{orderId}", consumes="application/json")
  //public Mono<TacoOrder> putOrder(@RequestBody Mono<TacoOrder> order) {
  //  return order.flatMap(repo::save);
  //}

  @PutMapping(path="/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> updateOrder(@PathVariable String orderId, @RequestBody TacoOrder order){
    if(order.getId()!= null && !orderId.equals(order.getId())){
      return Mono.just(ResponseEntity.badRequest().build());
    }

    return repo.findById(orderId).flatMap(existingOrder ->{

      existingOrder.setDeliveryName(order.getDeliveryName());
      existingOrder.setDeliveryStreet(order.getDeliveryStreet());
      existingOrder.setDeliveryCity(order.getDeliveryCity());
      existingOrder.setDeliveryState(order.getDeliveryState());
      existingOrder.setDeliveryZip(order.getDeliveryZip());

      existingOrder.setTacos(order.getTacos());


      return repo.save(existingOrder);
    }).map(savedOrder->ResponseEntity.ok(savedOrder)) //200
    .defaultIfEmpty(ResponseEntity.notFound().build()); //404

  }

  @PatchMapping(path="/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> patchOrder(@PathVariable("orderId") String orderId,
                          @RequestBody whiteListOrderApiController patch) {

    return repo.findById(orderId)
        .flatMap(order -> {
          if (patch.getDeliveryName() != null) {
            order.setDeliveryName(patch.getDeliveryName());
          }
          if (patch.getDeliveryStreet() != null) {
            order.setDeliveryStreet(patch.getDeliveryStreet());
          }
          if (patch.getDeliveryCity() != null) {
            order.setDeliveryCity(patch.getDeliveryCity());
          }
          if (patch.getDeliveryState() != null) {
            order.setDeliveryState(patch.getDeliveryState());
          }
          if (patch.getDeliveryZip() != null) {
            order.setDeliveryZip(patch.getDeliveryZip());
          }
          /*if (patch.getCcNumber() != null) {
            order.setCcNumber(patch.getCcNumber());
          }
          if (patch.getCcExpiration() != null) {
            order.setCcExpiration(patch.getCcExpiration());
          }
          if (patch.getCcCVV() != null) {
            order.setCcCVV(patch.getCcCVV());
          }*/
          return repo.save(order);
        })
        .map(savedOrder -> ResponseEntity.ok(savedOrder)) // 200 OK
        .defaultIfEmpty(ResponseEntity.notFound().build()); //404 if doesnt find anything
  }

  @DeleteMapping("/{orderId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable String orderId) {
    return repo.findById(orderId).flatMap(orderToDelete ->{
      return repo.delete(orderToDelete).thenReturn(ResponseEntity.noContent().<Void>build()); //204 no content
    }).defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
