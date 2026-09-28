package tacos.kitchen.messaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "processed_events")
public class ProcessedEvent {
    @Id
    private String eventId;
    private Date processedAt;

    public ProcessedEvent(String eventId) {
        this.eventId = eventId;
        this.processedAt = new Date();
    }
}