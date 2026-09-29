package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import tacos.TacoOrder;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderMessagingService;
import tacos.core.OrderPlacementService;
import tacos.data.MongoTransactionConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import tacos.outbox.OutboxPublisher;
import tacos.outbox.OutboxEvent;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = OutboxTransactionTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
public class OutboxTransactionTest {

    
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableReactiveMongoRepositories(basePackages = "tacos.data")
    @Import({
        OrderPlacementService.class,
        OutboxPublisher.class,
        MongoTransactionConfig.class
    })
    static class TestConfig {
        @Bean
        @Primary
        public OrderMessagingService messagingServiceMock() {
            return mock(OrderMessagingService.class);
        }
    }

    @Autowired
    private OrderPlacementService placementService;

    @Autowired
    private OutboxPublisher outboxPublisher;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private OrderMessagingService messagingServiceMock;

    @BeforeEach
    public void cleanup() {
        mongoTemplate.remove(new Query(), TacoOrder.class).block();
        mongoTemplate.remove(new Query(), OutboxEvent.class).block();
        reset(messagingServiceMock);
    }

    @Test
    public void testPlaceOrder_SavesOrderAndOutboxEvent() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Bruce Wayne");
        order.setDeliveryCity("Gotham");

        TacoOrder savedOrder = placementService.placeOrderTransactionally(order, null, "test-user").block(Duration.ofSeconds(5));

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();

        OutboxEvent outboxEvent = mongoTemplate.findOne(new Query(), OutboxEvent.class).block(Duration.ofSeconds(5));
        
        assertThat(outboxEvent).isNotNull();
        assertThat(outboxEvent.getState()).isEqualTo("NEW");
        assertThat(outboxEvent.getEvent().getPayload().getDeliveryName()).isEqualTo("Bruce Wayne");
    }

    @Test
    public void testPublisherProcessesOutboxEvents() {
        TacoOrder order = new TacoOrder();
        order.setId("ORD_TEST_123");
        order.setDeliveryName("Clark Kent");

        OrderEvent event = new OrderEvent(
            tacos.messaging.contract.OrderEventType.ORDER_CREATED, 
            "ORD_TEST_123", 
            tacos.messaging.contract.OrderEventPayload.fromDomain(order)
        );

        OutboxEvent outbox = new OutboxEvent();
        outbox.setId(event.getEventId());
        outbox.setEvent(event);
        outbox.setState("NEW");
        outbox.setRetries(0);
        outbox.setCreatedAt(new java.util.Date());
        outbox.setUpdatedAt(new java.util.Date());

        mongoTemplate.save(outbox).block(Duration.ofSeconds(5));

        outboxPublisher.processOutbox();

        verify(messagingServiceMock, timeout(2000).times(1)).sendOrderEvent(any(OrderEvent.class));

        OutboxEvent processedEvent = mongoTemplate.findById(event.getEventId(), OutboxEvent.class).block(Duration.ofSeconds(5));
        assertThat(processedEvent.getState()).isEqualTo("PUBLISHED");
    }
}