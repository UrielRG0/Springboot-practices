package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.InventoryService;
import tacos.OrderStatus;
import tacos.OrderWorkflowService;
import tacos.User;
import tacos.core.OrderPlacementService;
import tacos.data.OrderRepository;
import tacos.api.dto.OrderCreateRequest;
import tacos.api.dto.OrderDetailDTO;
import tacos.api.dto.OrderPatchRequest;
import tacos.api.dto.OrderSummaryDTO;
import tacos.messaging.contract.OrderMessagingService;
import tacos.messaging.contract.OrderEventPayload;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderEventType;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(path="/api", produces="application/json")
@CrossOrigin(origins="http://localhost:8080")
public class OrderApiController {

  private OrderRepository repo;
  private OrderMessagingService orderMessages;
  private EmailOrderService emailOrderService;
  private PaymentGateway paymentGateway;
  private OrderPricingService pricingService;
  private InventoryService inventoryService;
  private OrderWorkflowService workflowService;
  private OrderPlacementService placementService; 

  public OrderApiController(OrderRepository repo,
                            OrderMessagingService orderMessages,
                            EmailOrderService emailOrderService,
                            PaymentGateway paymentGateway,
                            OrderPricingService pricingService,
                            InventoryService inventoryService,
                            OrderWorkflowService workflowService,
                            OrderPlacementService placementService) { 
    this.repo = repo;
    this.orderMessages = orderMessages;
    this.emailOrderService = emailOrderService;
    this.paymentGateway = paymentGateway;
    this.pricingService = pricingService;
    this.inventoryService = inventoryService;
    this.workflowService = workflowService;
    this.placementService = placementService;
  }

  @GetMapping(path = "/users/me/orders")
  public Flux<OrderSummaryDTO> myOrders(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size,
          @AuthenticationPrincipal User loggedUser) {
      
      if (loggedUser == null) return Flux.empty();

      org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
              page, size, org.springframework.data.domain.Sort.by(
                      org.springframework.data.domain.Sort.Direction.DESC, "placedAt", "id"));
      return repo.findByUserOrderByPlacedAtDesc(loggedUser, pageable).map(this::toSummaryDTO);
  }

  @GetMapping(path = "/users/me/orders/{id}")
  public Mono<ResponseEntity<OrderDetailDTO>> myOrderDetail(
          @PathVariable String id,
          @AuthenticationPrincipal User loggedUser) {
      
      if (loggedUser == null) return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

      return repo.findById(id)
              .filter(order -> order.getUser() != null && order.getUser().getId().equals(loggedUser.getId()))
              .map(order -> ResponseEntity.ok(toDetailDTO(order)))
              .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping(path = "/admin/orders")
  public Flux<OrderSummaryDTO> adminOrders(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size,
          @AuthenticationPrincipal User loggedUser) {
      
      if (loggedUser == null || loggedUser.getRole() == null || !loggedUser.getRole().contains("ADMIN")) {
          return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado"));
      }

      org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
              page, size, org.springframework.data.domain.Sort.by(
                      org.springframework.data.domain.Sort.Direction.DESC, "placedAt", "id"));

      return repo.findAllBy(pageable).map(this::toSummaryDTO);
  }

  @PostMapping(path="/orders", consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TacoOrder> postOrder(
          @Valid @RequestBody OrderCreateRequest request, 
          @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, 
          @AuthenticationPrincipal User loggedUser) {
    
    TacoOrder order = tacos.api.dto.OrderMapper.toDomainOrder(request);
    String userId = "anonymous";
    
    if (loggedUser != null) {
        order.setUser(loggedUser); 
        userId = loggedUser.getId() != null ? loggedUser.getId() : "anonymous";
    }
    
    String finalUserId = userId;

    return paymentGateway.tokenize(request.getPaymentToken(), loggedUser)
      .flatMap(safePaymentMethod -> {
          order.setPaymentMethod(safePaymentMethod);
          return pricingService.calculatePrices(order); 
      })
      .flatMap(pricedOrder -> 
          inventoryService.reserveInventory(pricedOrder).thenReturn(pricedOrder)
      )
      .flatMap(pricedOrder -> placementService.placeOrderTransactionally(pricedOrder, idempotencyKey, finalUserId))
      .onErrorResume(e -> {
          if (e instanceof ResponseStatusException) return Mono.error(e);
          if (e.getMessage() != null && e.getMessage().contains("INSUFFICIENT_STOCK")) {
              return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
          }
          if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
              return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()));
          }
          return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
      });
  }

  @PostMapping(path="/orders/fromEmail", consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TacoOrder> postOrderFromEmail(@RequestBody EmailOrder emailOrder) { 
      return emailOrderService.convertEmailOrderToDomainOrder(Mono.just(emailOrder))
          .flatMap(order -> repo.save(order))
          .doOnNext(savedOrder -> {
              try {
                  OrderEventPayload payload = OrderEventPayload.fromDomain(savedOrder);
                  OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, savedOrder.getId(), payload);
                  orderMessages.sendOrderEvent(event);
              } catch (Exception ex) {
                  System.err.println(ex.getMessage());
              }
          })
          .onErrorResume(e -> {
              if (e instanceof IllegalArgumentException) {
                  return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()));
              }
              return Mono.error(e);
          });
  }

  @PutMapping(path="/orders/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> updateOrder(@PathVariable String orderId, @Valid @RequestBody OrderCreateRequest orderDto,
      @AuthenticationPrincipal User loggedUser){
    return repo.findById(orderId).flatMap(existingOrder ->{
      
      boolean isOwner = existingOrder.getUser() != null && loggedUser != null && existingOrder.getUser().getId().equals(loggedUser.getId());
      boolean isAdmin = loggedUser != null && loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");
      
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

  @PatchMapping(path="/orders/{orderId}", consumes="application/json")
  public Mono<ResponseEntity<TacoOrder>> patchOrder(@PathVariable("orderId") String orderId,@Valid @RequestBody OrderPatchRequest patch, @AuthenticationPrincipal User loggedUser) {
    return repo.findById(orderId)
      .flatMap(order -> {
        boolean isOwner = order.getUser() != null && loggedUser != null && order.getUser().getId().equals(loggedUser.getId());
        boolean isAdmin = loggedUser != null && loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");
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

  @DeleteMapping("/orders/{orderId}")
  public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable String orderId, 
                                                @AuthenticationPrincipal User loggedUser) {
    return repo.findById(orderId).flatMap(orderToDelete -> {
      boolean isOwner = orderToDelete.getUser() != null && loggedUser != null && orderToDelete.getUser().getId().equals(loggedUser.getId());
      boolean isAdmin = loggedUser != null && loggedUser.getRole() != null && loggedUser.getRole().contains("ADMIN");

      if (!isOwner && !isAdmin) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build());
      }
      if (orderToDelete.getStatus() != null && orderToDelete.getStatus().equals("PREPARING")) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).<Void>build());
      }
      return inventoryService.releaseInventory(orderToDelete)
          .then(repo.delete(orderToDelete))
          .thenReturn(ResponseEntity.noContent().<Void>build());
        
    }).defaultIfEmpty(ResponseEntity.notFound().build()); 
  }

  @PostMapping(path="/orders/{orderId}/reorder", consumes="application/json")
  public Mono<ResponseEntity<Object>> reorder(
          @PathVariable String orderId,
          @RequestBody tacos.api.dto.ReorderRequest request,
          @AuthenticationPrincipal User loggedUser) {
      
      if (loggedUser == null) {
          return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
      }

      return repo.findById(orderId)
          .filter(oldOrder -> oldOrder.getUser() != null && oldOrder.getUser().getId().equals(loggedUser.getId()))
          .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
          .flatMap(oldOrder -> {
              TacoOrder newOrder = new TacoOrder(); 
              newOrder.setUser(loggedUser);
              newOrder.setDeliveryName(oldOrder.getDeliveryName());
              newOrder.setDeliveryStreet(oldOrder.getDeliveryStreet());
              newOrder.setDeliveryCity(oldOrder.getDeliveryCity());
              newOrder.setDeliveryState(oldOrder.getDeliveryState());
              newOrder.setDeliveryZip(oldOrder.getDeliveryZip());
              newOrder.setItems(oldOrder.getItems()); 
              return pricingService.calculatePrices(newOrder).flatMap(pricedOrder -> {
                      
                      BigDecimal oldTotal = oldOrder.getTotal() != null ? oldOrder.getTotal() : BigDecimal.ZERO;
                      BigDecimal newTotal = pricedOrder.getTotal() != null ? pricedOrder.getTotal() : BigDecimal.ZERO;
                      if (oldTotal.compareTo(newTotal) != 0 && !request.isConfirmPriceChange()) {
                          tacos.api.dto.ReorderQuoteDTO quote = new tacos.api.dto.ReorderQuoteDTO();
                          quote.setMessage("The price of the product has been changed");
                          quote.setOldTotal(oldTotal);
                          quote.setNewTotal(newTotal);
                          quote.setRequiresConfirmation(true);
                          return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body((Object) quote));
                      }
                      
                      return paymentGateway.tokenize(request.getPaymentToken(), loggedUser)
                          .flatMap(safePaymentMethod -> {
                              pricedOrder.setPaymentMethod(safePaymentMethod); 
                              return inventoryService.reserveInventory(pricedOrder).thenReturn(pricedOrder);
                          })
                          .flatMap(repo::save)
                          .doOnNext(savedOrder -> {
                              try {
                                  OrderEventPayload payload = OrderEventPayload.fromDomain(savedOrder);
                                  OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, savedOrder.getId(), payload);
                                  orderMessages.sendOrderEvent(event); 
                              } catch (Exception ex) {
                                  System.err.println(ex.getMessage());
                              }
                          })
                          .map(savedOrder -> ResponseEntity.status(HttpStatus.CREATED).body((Object) toSummaryDTO(savedOrder)));
              });
          })
          .onErrorResume(e -> {
              if (e instanceof ResponseStatusException) return Mono.error(e);
              if (e.getMessage() != null && e.getMessage().contains("INSUFFICIENT_STOCK")) {
                  return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
              }
              return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
          });
  }

  private OrderSummaryDTO toSummaryDTO(TacoOrder order) {
      OrderSummaryDTO dto = new OrderSummaryDTO();
      dto.setId(order.getId());
      dto.setPlacedAt(order.getPlacedAt());
      dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null); 
      dto.setTotal(order.getTotal());
      dto.setDeliveryName(order.getDeliveryName());
      return dto;
  }

  private OrderDetailDTO toDetailDTO(TacoOrder order) {
      OrderDetailDTO dto = new OrderDetailDTO();
      dto.setId(order.getId());
      dto.setPlacedAt(order.getPlacedAt());
      dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
      dto.setTotal(order.getTotal());
      dto.setDeliveryName(order.getDeliveryName());
      dto.setDeliveryStreet(order.getDeliveryStreet());
      dto.setDeliveryCity(order.getDeliveryCity());
      dto.setDeliveryState(order.getDeliveryState());
      dto.setDeliveryZip(order.getDeliveryZip());
      dto.setItems(order.getItems());
      return dto;
  }

  @PostMapping(path="/orders/{orderId}/cancel", consumes="application/json")
  public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<tacos.api.dto.OrderSummaryDTO>> cancelOrder(
          @PathVariable String orderId,
          @RequestBody(required = false) java.util.Map<String, String> payload,
          @AuthenticationPrincipal User loggedUser) {
      
      String reason = payload != null && payload.containsKey("reason") ? payload.get("reason") : "Canceled by user";

      return repo.findById(orderId)
          .switchIfEmpty(reactor.core.publisher.Mono.<TacoOrder>error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
          .map((TacoOrder order) -> workflowService.transitionStatus(order, OrderStatus.CANCELLED, loggedUser, reason))
          .flatMap(repo::save)
          .onErrorResume(org.springframework.dao.OptimisticLockingFailureException.class, 
                  e -> reactor.core.publisher.Mono.error(new ResponseStatusException(HttpStatus.CONFLICT)))
          .map(saved -> ResponseEntity.ok(toSummaryDTO(saved)));
  }

  @PatchMapping(path="/orders/{orderId}/status", consumes="application/json")
  public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<tacos.api.dto.OrderSummaryDTO>> updateStatus(
          @PathVariable String orderId,
          @RequestBody tacos.api.dto.OrderStatusUpdateRequest request,
          @AuthenticationPrincipal User loggedUser) {
      
      return repo.findById(orderId)
          .switchIfEmpty(reactor.core.publisher.Mono.<TacoOrder>error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
          .map((TacoOrder order) -> workflowService.transitionStatus(order, request.getStatus(), loggedUser, request.getReason()))
          .flatMap(repo::save)
          .onErrorResume(org.springframework.dao.OptimisticLockingFailureException.class, 
                  e -> reactor.core.publisher.Mono.error(new ResponseStatusException(HttpStatus.CONFLICT)))
          .map(saved -> ResponseEntity.ok(toSummaryDTO(saved)));
  }
}