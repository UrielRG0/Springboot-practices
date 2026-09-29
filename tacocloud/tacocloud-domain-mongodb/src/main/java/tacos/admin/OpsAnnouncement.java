package tacos.admin;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Data
@Document(collection = "ops_announcements")
public class OpsAnnouncement {
    @Id
    private String id;
    private String text;
    private String severity; 
    private Instant createdAt;
    private Instant expiresAt;
    private String createdBy;
    private boolean active;
}