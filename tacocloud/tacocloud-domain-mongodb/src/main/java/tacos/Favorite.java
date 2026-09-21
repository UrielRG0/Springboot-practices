package tacos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Document
@CompoundIndex(name = "user_taco_idx", def = "{'userId': 1, 'tacoId': 1}", unique = true)
public class Favorite {
    
    @Id
    private String id;
    
    private String userId;
    private String tacoId;
    
    private Date createdAt = new Date();
}