package tacos;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.OrderAuditLog;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;

@Service
public class KitchenQueueService {

    private final ReactiveMongoTemplate mongoTemplate;

    public KitchenQueueService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Flux<TacoOrder> getQueue() {
        Query query = new Query(Criteria.where("status").is(OrderStatus.CREATED))
                .with(Sort.by(Sort.Direction.ASC, "placedAt", "id"));
        return mongoTemplate.find(query, TacoOrder.class);
    }

    public Mono<TacoOrder> claimNextOrder(User cook) {

        Query query = new Query(Criteria.where("status").is(OrderStatus.CREATED))
                .with(Sort.by(Sort.Direction.ASC, "placedAt", "id"));

        Update update = new Update()
                .set("status", OrderStatus.ACCEPTED)
                .set("cookId", cook.getUsername())
                .push("statusHistory", new OrderAuditLog(OrderStatus.CREATED, OrderStatus.ACCEPTED, cook.getUsername(), "Claimed by kitchen station"));

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query, update, options, TacoOrder.class)
                .flatMap(this::calculateAndSetEta);
    }

    private Mono<TacoOrder> calculateAndSetEta(TacoOrder order) {
        Query inKitchenQuery = new Query(Criteria.where("status").in(OrderStatus.ACCEPTED, OrderStatus.PREPARING));
        
        return mongoTemplate.count(inKitchenQuery, TacoOrder.class)
                .map(ordersInPrep -> {
                    int baseTime = ordersInPrep.intValue() * 3;
                    int complexityTime = (order.getItems() != null ? order.getItems().size() : 1) * 2;
                    order.setEstimatedPrepMinutes(baseTime + complexityTime);
                    return order;
                })
                .flatMap(mongoTemplate::save);
    }
}