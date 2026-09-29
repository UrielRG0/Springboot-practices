package tacos.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.Date;

@Data
@Document(collection = "idempotency_records")
public class IdempotencyRecord {
    @Id
    private String id; 
    private String userId;
    private String idempotencyKey;
    private String requestHash;
    private String orderId;
    private Date createdAt;
}