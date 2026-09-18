package tacos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document
@Data
@NoArgsConstructor(force=true) 
@AllArgsConstructor 
public class PaymentMethod {

  @Id
  private String id;
  
  private User user; 

  @JsonIgnore 
  private String paymentToken; 
  
  private String brand; 
  private String last4;  
  
}