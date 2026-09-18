package tacos;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@Document
@NoArgsConstructor 
public class Ingredient {

  @Id
  private String id;   
  private String name;
  private Type type;   
  
  private Set<DietaryTag> dietaryTags = new HashSet<>();
  private Set<Allergen> allergens = new HashSet<>();
  private int spiceLevel = 0; 

  public enum Type {
    WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
  }

  @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
  private BigDecimal unitPrice = BigDecimal.ZERO;
  
  private boolean available = true;
  
  @Min(value = 0, message = "El stock no puede ser negativo")
  private int stockOnHand = 0;
  
  @Min(value = 0, message = "El nivel de reorden no puede ser negativo")
  private int reorderLevel = 0;

  @Version
  private Long version;

  public Ingredient(String id, String name, Type type) {
      this.id = id;
      this.name = name;
      this.type = type;
  }
}