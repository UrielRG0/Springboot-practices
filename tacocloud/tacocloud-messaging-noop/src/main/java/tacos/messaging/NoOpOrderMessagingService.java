package tacos.messaging;

import org.springframework.stereotype.Service;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderMessagingService; 

@Service
public class NoOpOrderMessagingService implements OrderMessagingService {

    @Override
    public void sendOrderEvent(OrderEvent event) {
        System.out.println("Sending secure event " + event.getEventType() + 
                           " for the order " + event.getCorrelationId());
    }
}