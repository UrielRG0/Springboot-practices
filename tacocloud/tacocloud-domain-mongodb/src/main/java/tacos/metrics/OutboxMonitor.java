package tacos.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class OutboxMonitor implements ReactiveHealthIndicator {

    private final ReactiveMongoTemplate mongoTemplate;
    private final AtomicLong pendingOutboxCount;

    public OutboxMonitor(ReactiveMongoTemplate mongoTemplate, MeterRegistry registry) {
        this.mongoTemplate = mongoTemplate;
        this.pendingOutboxCount = new AtomicLong(0);
        
        Gauge.builder("tacocloud.outbox.pending", pendingOutboxCount, AtomicLong::get)
             .description("Eventos atascados en el outbox")
             .tag("component", "outbox")
             .register(registry);
    }

    @Scheduled(fixedRate = 5000)
    public void updatePendingCount() {
        Query query = new Query(Criteria.where("state").is("NEW"));
        mongoTemplate.count(query, "outbox_events")
            .doOnNext(pendingOutboxCount::set)
            .subscribe();
    }

    @Override
    public Mono<Health> health() {
        long pending = pendingOutboxCount.get();
        if (pending > 500) {
            return Mono.just(Health.down()
                    .withDetail("reason", "Outbox saturado, el publicador podría estar caído")
                    .withDetail("pending_events", pending)
                    .build());
        }
        return Mono.just(Health.up()
                .withDetail("pending_events", pending)
                .withDetail("status", "Operación normal")
                .build());
    }
}