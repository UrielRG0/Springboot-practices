package tacos.outbox;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import tacos.messaging.contract.OrderEvent;
import java.util.Date;

@Data
@Document(collection = "outbox_events")
public class OutboxEvent {
    @Id
    private String id; 
    private OrderEvent event;
    private String state; 
    private int retries;
    private Date createdAt;
    private Date updatedAt;
}