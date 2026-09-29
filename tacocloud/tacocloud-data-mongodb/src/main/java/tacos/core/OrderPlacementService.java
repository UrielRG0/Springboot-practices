package tacos.core;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.outbox.OutboxEvent;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderEventPayload;
import tacos.messaging.contract.OrderEventType;

import java.time.Duration;
import java.util.Date;

@Service
public class OrderPlacementService {

    private final OrderRepository orderRepo;
    private final ReactiveMongoTemplate mongoTemplate;
    private final TransactionalOperator transactionalOperator;

    public OrderPlacementService(OrderRepository orderRepo, 
                                 ReactiveMongoTemplate mongoTemplate,
                                 ReactiveTransactionManager transactionManager) {
        this.orderRepo = orderRepo;
        this.mongoTemplate = mongoTemplate;
        this.transactionalOperator = TransactionalOperator.create(transactionManager);
    }

    public Mono<TacoOrder> placeOrderTransactionally(TacoOrder order, String idempotencyKey, String userId) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            Mono<TacoOrder> normalExecution = executeStandardPlacement(order);
            return applyTransactionOrFallback(normalExecution);
        }

        String recordId = userId + ":" + idempotencyKey;
        String currentHash = CanonicalHasher.hashOrder(order);

        return mongoTemplate.findById(recordId, IdempotencyRecord.class)
            .flatMap(existing -> {
                if (existing.getRequestHash().equals(currentHash)) {
                    return orderRepo.findById(existing.getOrderId());
                } else {
                    return Mono.<TacoOrder>error(new ResponseStatusException(HttpStatus.CONFLICT, "Idempotency key alredy used in a diferent order"));
                }
            })
            .switchIfEmpty(Mono.defer(() -> {
                Mono<TacoOrder> idempotencyExecution = executeStandardPlacement(order)
                    .flatMap(savedOrder -> {
                        IdempotencyRecord record = new IdempotencyRecord();
                        record.setId(recordId);
                        record.setUserId(userId);
                        record.setIdempotencyKey(idempotencyKey);
                        record.setRequestHash(currentHash);
                        record.setOrderId(savedOrder.getId());
                        record.setCreatedAt(new Date());
                        
                        return mongoTemplate.insert(record).thenReturn(savedOrder);
                    });
                
                return applyTransactionOrFallback(idempotencyExecution);
            }))
            .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(100))
                .filter(e -> e instanceof DuplicateKeyException));
    }

    private Mono<TacoOrder> applyTransactionOrFallback(Mono<TacoOrder> execution) {
        return execution
            .as(transactionalOperator::transactional)
            .onErrorResume(e -> {
                if (e.getMessage() != null && (e.getMessage().contains("Sessions are not supported") || e.getMessage().contains("Transaction") || e.getCause() instanceof com.mongodb.MongoClientException)) {
                    return execution;
                }
                return Mono.error(e);
            });
    }

    private Mono<TacoOrder> executeStandardPlacement(TacoOrder order) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "UNKNOWN_CORRELATION_ID");

            return orderRepo.save(order)
                .flatMap(savedOrder -> {
                    OrderEventPayload payload = OrderEventPayload.fromDomain(savedOrder);
                    OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, savedOrder.getId(), payload);
                    event.setCorrelationId(correlationId); 

                    OutboxEvent outbox = new OutboxEvent();
                    outbox.setId(event.getEventId());
                    outbox.setEvent(event);
                    outbox.setState("NEW");
                    outbox.setRetries(0);
                    outbox.setCreatedAt(new Date());
                    outbox.setUpdatedAt(new Date());

                    return mongoTemplate.save(outbox).thenReturn(savedOrder);
                });
        });
    }
}