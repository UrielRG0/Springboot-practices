package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderEventPayload;
import tacos.messaging.contract.OrderEventType;
import tacos.data.MongoTransactionConfig;

import tacos.kitchen.messaging.IdempotentOrderProcessor;
import tacos.kitchen.messaging.IdempotentOrderProcessor;
import tacos.kitchen.messaging.RabbitOrderListener;
import tacos.kitchen.messaging.ProcessedEvent;


import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = IdempotentConsumerTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
public class IdempotentConsumerTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class
    })
    @EnableReactiveMongoRepositories(basePackages = "tacos.data")
    @Import({
        IdempotentOrderProcessor.class,
        RabbitOrderListener.class,
        MongoTransactionConfig.class
    })
    static class TestConfig {
    }

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private IdempotentOrderProcessor processor;

    @Autowired
    private RabbitOrderListener listener;

    @BeforeEach
    public void setup() {
        mongoTemplate.remove(new Query(), TacoOrder.class).block();
        mongoTemplate.remove(new Query(), ProcessedEvent.class).block();
    }

    @Test
    public void testDoubleDelivery_IsIdempotent() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Peter Parker");
        order.setStatus(OrderStatus.CREATED);
        TacoOrder savedOrder = mongoTemplate.save(order).block(Duration.ofSeconds(5));

        OrderEvent event = new OrderEvent(
            OrderEventType.ORDER_CREATED,
            savedOrder.getId(),
            OrderEventPayload.fromDomain(savedOrder)
        );

        listener.receiveOrder(event);

        TacoOrder updatedOrder = mongoTemplate.findById(savedOrder.getId(), TacoOrder.class).block(Duration.ofSeconds(5));
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PREPARING);

        long count = mongoTemplate.count(new Query(), ProcessedEvent.class).block(Duration.ofSeconds(5));
        assertThat(count).isEqualTo(1);

        listener.receiveOrder(event);

        long countAfter = mongoTemplate.count(new Query(), ProcessedEvent.class).block(Duration.ofSeconds(5));
        assertThat(countAfter).isEqualTo(1); 
    }

    @Test
    public void testUnknownVersion_ThrowsAmqpReject_GoesToDLQ() {
        TacoOrder order = new TacoOrder();
        order.setId("ORD_999");
        
        OrderEvent event = new OrderEvent(
            OrderEventType.ORDER_CREATED,
            "ORD_999",
            OrderEventPayload.fromDomain(order)
        );
        event.setVersion("v2"); 

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> {
            listener.receiveOrder(event);
        });
    }
}