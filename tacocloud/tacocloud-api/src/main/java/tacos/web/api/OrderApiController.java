package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

import tacos.OrderItem;

@RestController
@RequestMapping(path="/api/orders", produces="application/json")
@CrossOrigin(origins="http://localhost:8080")
public class OrderApiController {

  private OrderRepository repo;
  private OrderMessagingService orderMessages;
  private EmailOrderService emailOrderService;
  private PaymentGateway paymentGateway;
  
  private OrderPricingService pricingService;

  public OrderApiController(OrderRepository repo,
                            OrderMessagingService orderMessages,
                            EmailOrderService emailOrderService,
                            PaymentGateway paymentGateway,
                            OrderPricingService pricingService) { 
    this.repo = repo;
    this.orderMessages = orderMessages;
    this.emailOrderService = emailOrderService;
    this.paymentGateway = paymentGateway;
    this.pricingService = pricingService;
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

  @PostMapping(consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TacoOrder> postOrder(@Valid @RequestBody OrderCreateRequest request, @AuthenticationPrincipal User loggedUser) {
    TacoOrder order = tacos.api.dto.OrderMapper.toDomainOrder(request);
    order.setUser(loggedUser); 
    
    return paymentGateway.tokenize(request.getPaymentToken(), loggedUser)
      .flatMap(safePaymentMethod -> {
          order.setPaymentMethod(safePaymentMethod);
          return pricingService.calculatePrices(order); 
      })
      .flatMap(pricedOrder -> {
          orderMessages.sendOrder(pricedOrder);  
          return repo.save(pricedOrder);
      });
  }

  @PostMapping(path="/fromEmail", consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TacoOrder> postOrderFromEmail(@RequestBody Mono<EmailOrder> emailOrder){
    return emailOrderService.convertEmailOrderToDomainOrder(emailOrder)
      .flatMap(order -> pricingService.calculatePrices(order)) 
      .flatMap(pricedOrder -> repo.save(pricedOrder))
      .doOnNext(savedOrder -> orderMessages.sendOrder(savedOrder));
  }

  @PutMapping(path="/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> updateOrder(@PathVariable String orderId, @Valid @RequestBody OrderCreateRequest orderDto,
      @AuthenticationPrincipal User loggedUser){
    return repo.findById(orderId).flatMap(existingOrder ->{
      
      boolean isOwner = existingOrder.getUser() != null && existingOrder.getUser().getId().equals(loggedUser.getId());
      boolean isAdmin = loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");
      
      if (!isOwner && !isAdmin) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).<TacoOrder>build());
      }

      existingOrder.setDeliveryName(orderDto.getDeliveryName());
      existingOrder.setDeliveryStreet(orderDto.getDeliveryStreet());
      existingOrder.setDeliveryCity(orderDto.getDeliveryCity());
      existingOrder.setDeliveryState(orderDto.getDeliveryState());
      existingOrder.setDeliveryZip(orderDto.getDeliveryZip());
      
      existingOrder.setItems(orderDto.getItems()); 
      return pricingService.calculatePrices(existingOrder)
        .flatMap(pricedOrder -> repo.save(pricedOrder))
        .map(savedOrder -> ResponseEntity.ok(savedOrder));
      
    }).defaultIfEmpty(ResponseEntity.notFound().build()); 
  }

  @PatchMapping(path="/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> patchOrder(@PathVariable("orderId") String orderId, @Valid @RequestBody OrderPatchRequest patch, @AuthenticationPrincipal User loggedUser) {
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