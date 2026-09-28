package tacos.core;

import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.outbox.OutboxEvent;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderEventPayload;
import tacos.messaging.contract.OrderEventType;

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

    public Mono<TacoOrder> placeOrderTransactionally(TacoOrder order) {
        Mono<TacoOrder> execution = orderRepo.save(order)
            .flatMap(savedOrder -> {
                OrderEventPayload payload = OrderEventPayload.fromDomain(savedOrder);
                OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, savedOrder.getId(), payload);

                OutboxEvent outbox = new OutboxEvent();
                outbox.setId(event.getEventId());
                outbox.setEvent(event);
                outbox.setState("NEW");
                outbox.setRetries(0);
                outbox.setCreatedAt(new Date());
                outbox.setUpdatedAt(new Date());

                return mongoTemplate.save(outbox).thenReturn(savedOrder);
            });

        return execution
            .as(transactionalOperator::transactional)
            .onErrorResume(e -> {
                if (e.getMessage() != null && (e.getMessage().contains("Sessions are not supported") || e.getMessage().contains("Transaction"))) {
                    return execution;
                }
                return Mono.error(e);
            });
    }
}