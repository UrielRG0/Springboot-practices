package tacos.kitchen.messaging;

import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.messaging.contract.OrderEvent;

@Service
public class IdempotentOrderProcessor {

    private final ReactiveMongoTemplate mongoTemplate;
    private final TransactionalOperator transactionalOperator;

    public IdempotentOrderProcessor(ReactiveMongoTemplate mongoTemplate, ReactiveTransactionManager transactionManager) {
        this.mongoTemplate = mongoTemplate;
        this.transactionalOperator = TransactionalOperator.create(transactionManager);
    }

    public Mono<Void> processSafely(OrderEvent event) {
        if (!"v1".equals(event.getVersion())) {
            return Mono.error(new IllegalArgumentException("version not supported " + event.getVersion()));
        }

        return mongoTemplate.exists(Query.query(Criteria.where("_id").is(event.getEventId())), ProcessedEvent.class)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.empty(); 
                }

                Query orderQuery = Query.query(Criteria.where("_id").is(event.getPayload().getOrderId()));
                Update updateStatus = new Update().set("status", tacos.OrderStatus.PREPARING);

                Mono<Void> execution = mongoTemplate.updateFirst(orderQuery, updateStatus, TacoOrder.class)
                    .then(mongoTemplate.save(new ProcessedEvent(event.getEventId())))
                    .then();

                return execution
                    .as(transactionalOperator::transactional)
                    .onErrorResume(e -> {
                        if (e.getMessage() != null && e.getMessage().contains("Sessions are not supported")) {
                            return execution;
                        }
                        return Mono.error(e);
                    });
            });
    }
}