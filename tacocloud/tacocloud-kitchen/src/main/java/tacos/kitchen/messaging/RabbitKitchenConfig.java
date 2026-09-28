package tacos.kitchen.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitKitchenConfig {

    public static final String QUEUE_NAME = "tacocloud.orders.queue";
    public static final String DLQ_NAME = "tacocloud.orders.dlq";
    public static final String EXCHANGE_NAME = "tacocloud.orders.exchange";

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue ordersQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ_NAME)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public Binding binding(Queue ordersQueue, DirectExchange ordersExchange) {
        return BindingBuilder.bind(ordersQueue).to(ordersExchange).with("orders.routing.key");
    }
}