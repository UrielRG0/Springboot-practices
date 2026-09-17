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
import tacos.api.dto.OrderCreateRequest;
import tacos.api.dto.OrderPatchRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import tacos.User;

import javax.validation.Valid;
@RestController
@RequestMapping(path="/api/orders",
                produces="application/json")
@CrossOrigin(origins="http://localhost:8080")
public class OrderApiController {

  private OrderRepository repo;
  private OrderMessagingService orderMessages;
  private EmailOrderService emailOrderService;

  private PaymentGateway paymentGateway;

  public OrderApiController(OrderRepository repo,
                            OrderMessagingService orderMessages,
                            EmailOrderService emailOrderService,
                            PaymentGateway paymentGateway) {
    this.repo = repo;
    this.orderMessages = orderMessages;
    this.emailOrderService = emailOrderService;
    this.paymentGateway = paymentGateway;
  }

  @GetMapping(produces="application/json")
  public Flux<TacoOrder> allOrders(@AuthenticationPrincipal User loggedUser) {
    boolean isAdmin = loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");
    if (isAdmin) {
      return repo.findAll();
    } else {
      return repo.findAll().filter(order -> order.getUser() != null && order.getUser().getId().equals(loggedUser.getId()));
    }
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
  public Mono<TacoOrder> postOrder(@Valid @RequestBody OrderCreateRequest request, @AuthenticationPrincipal User loggedUser) {
    TacoOrder order = tacos.api.dto.OrderMapper.toDomainOrder(request);
    order.setUser(loggedUser); 
    return paymentGateway.tokenize(request.getPaymentToken(), loggedUser)
      .flatMap(safePaymentMethod -> {order.setPaymentMethod(safePaymentMethod);orderMessages.sendOrder(order);  
        return repo.save(order);
      });
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
  public Mono<ResponseEntity<TacoOrder>> updateOrder(@PathVariable String orderId, @Valid @RequestBody OrderCreateRequest order,
      @AuthenticationPrincipal User loggedUser){return repo.findById(orderId).flatMap(existingOrder ->{
      
      boolean isOwner = existingOrder.getUser() != null && existingOrder.getUser().getId().equals(loggedUser.getId());
      boolean isAdmin = loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");
      
      if (!isOwner && !isAdmin) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).<TacoOrder>build());
      }

      existingOrder.setDeliveryName(order.getDeliveryName());
      existingOrder.setDeliveryStreet(order.getDeliveryStreet());
      existingOrder.setDeliveryCity(order.getDeliveryCity());
      existingOrder.setDeliveryState(order.getDeliveryState());
      existingOrder.setDeliveryZip(order.getDeliveryZip());
      existingOrder.setTacos(order.getTacos());
      return repo.save(existingOrder).map(savedOrder -> ResponseEntity.ok(savedOrder));
      
    }).defaultIfEmpty(ResponseEntity.notFound().build()); 
  }

  @PatchMapping(path="/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> patchOrder(@PathVariable("orderId") String orderId,@Valid @RequestBody OrderPatchRequest patch, @AuthenticationPrincipal User loggedUser) {

    return repo.findById(orderId)
      .flatMap(order -> {
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(loggedUser.getId());
        boolean isAdmin = loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");
        if (!isOwner && !isAdmin) {
          return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).<TacoOrder>build());
        }
        if (patch.getDeliveryName() != null) order.setDeliveryName(patch.getDeliveryName());
        if (patch.getDeliveryStreet() != null) order.setDeliveryStreet(patch.getDeliveryStreet());
        if (patch.getDeliveryCity() != null) order.setDeliveryCity(patch.getDeliveryCity());
        if (patch.getDeliveryState() != null) order.setDeliveryState(patch.getDeliveryState());
        if (patch.getDeliveryZip() != null) order.setDeliveryZip(patch.getDeliveryZip());
        return repo.save(order).map(savedOrder -> ResponseEntity.ok(savedOrder));
              
      }).defaultIfEmpty(ResponseEntity.notFound().build()); 
  }

  @DeleteMapping("/{orderId}")
  public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable String orderId, 
                                                @AuthenticationPrincipal User loggedUser) {
    
    return repo.findById(orderId).flatMap(orderToDelete->{
      boolean isOwner = orderToDelete.getUser() != null && orderToDelete.getUser().getId().equals(loggedUser.getId());
      boolean isAdmin=loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");

      if (!isOwner && !isAdmin) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build());
      }
      if (orderToDelete.getStatus() != null && orderToDelete.getStatus().equals("PREPARING")) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).<Void>build());
      }
      return repo.delete(orderToDelete).thenReturn(ResponseEntity.noContent().<Void>build());
    }).defaultIfEmpty(ResponseEntity.notFound().build()); 
  }

}
