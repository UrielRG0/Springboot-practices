package tacos.kitchen.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tacos.messaging.contract.OrderEvent;

@Component
public class RabbitOrderListener {

    private final IdempotentOrderProcessor processor;

    public RabbitOrderListener(IdempotentOrderProcessor processor) {
        this.processor = processor;
    }

    @RabbitListener(queues = RabbitKitchenConfig.QUEUE_NAME)
    public void receiveOrder(OrderEvent event) {
        try {
            processor.processSafely(event).block();
        } catch (IllegalArgumentException e) {
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("Error permanente, directo a DLQ", e);
        } catch (Exception e) {
            throw new RuntimeException("Error transitory, try again", e);
        }
    }
}