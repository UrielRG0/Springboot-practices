package tacos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Document
@CompoundIndex(name = "user_taco_rating_idx", def = "{'userId': 1, 'tacoId': 1}", unique = true)
public class TacoRating {
    
    @Id
    private String id;
    
    private String userId;
    private String tacoId;
    
    @Min(1) @Max(5)
    private int score;
    
    private Date ratedAt = new Date();
}