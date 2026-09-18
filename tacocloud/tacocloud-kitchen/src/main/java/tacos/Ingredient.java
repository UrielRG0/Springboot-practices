package tacos;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class Ingredient {

  @Id
  private final String id;
  private final String name;
  private final Type type;

  public enum Type {
    WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
  }


  @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
  private BigDecimal unitPrice = BigDecimal.ZERO;
  
  private boolean available = true;
  
  @JsonIgnore 
  @Min(value = 0, message = "El stock no puede ser negativo")
  private int stockOnHand = 0;
  
  @JsonIgnore 
  @Min(value = 0, message = "El nivel de reorden no puede ser negativo")
  private int reorderLevel = 0;

  @JsonIgnore 
  @Version 
  private Long version;

  @PersistenceConstructor
  public Ingredient(String id, String name, Type type) {
      this.id = id;
      this.name = name;
      this.type = type;
  }
}