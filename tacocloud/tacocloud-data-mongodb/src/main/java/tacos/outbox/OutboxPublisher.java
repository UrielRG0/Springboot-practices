package tacos.outbox;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import tacos.messaging.contract.OrderMessagingService;

import java.util.Date;

@Service
public class OutboxPublisher {

    private final ReactiveMongoTemplate mongoTemplate;
    private final OrderMessagingService messagingService;
    private static final int MAX_RETRIES = 3;

    public OutboxPublisher(ReactiveMongoTemplate mongoTemplate, OrderMessagingService messagingService) {
        this.mongoTemplate = mongoTemplate;
        this.messagingService = messagingService;
    }

    // Se ejecuta cada 5 segundos
    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        Query query = new Query(Criteria.where("state").in("NEW", "FAILED").and("retries").lt(MAX_RETRIES));
        
        Update update = new Update()
                .set("state", "PUBLISHING")
                .inc("retries", 1)
                .set("updatedAt", new Date());

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);

        mongoTemplate.findAndModify(query, update, options, OutboxEvent.class)
            .flatMap(outbox -> {
                try {

                    messagingService.sendOrderEvent(outbox.getEvent());
                    outbox.setState("PUBLISHED");
                } catch (Exception e) {
                    outbox.setState("FAILED");
                }
                outbox.setUpdatedAt(new Date());
                return mongoTemplate.save(outbox); 
            })
            .subscribe(); 
    }
}