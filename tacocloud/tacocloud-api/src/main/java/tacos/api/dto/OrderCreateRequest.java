package tacos.api.dto;

import lombok.Data;
import java.util.List;
import tacos.Taco;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

@Data
public class OrderCreateRequest {
    
    @NotBlank(message = "Delivery name is required")
    @Size(min=10,max = 50, message = "Delivery name must be less than 50 characters")
    private String deliveryName;

    @NotBlank(message = "Delivery street is required")
    @Size(min=10,max = 50, message = "Delivery street must be less than 50 characters")
    private String deliveryStreet;

    @NotBlank (message = "Delivery city is required")
    @Size(min=10,max = 50, message = "Delivery city must be less than 50 characters")
    private String deliveryCity;

    @NotBlank (message = "Delivery state is required")
    @Size(min=2,max = 2, message = "Delivery state must be 2 characters")
    private String deliveryState;

    @NotBlank (message = "Delivery zip is required")
    @Size(min=5,max = 10, message = "Delivery zip must be less than 10 characters")
    private String deliveryZip;

    /*@NotBlank (message = "Credit card number is required")
    @Size(min=16,max = 16, message = "Credit card number must be 16 characters")
    private String ccNumber;

    @NotBlank (message = "Credit card expiration is required")
    @Size(min=5,max = 5, message = "Credit card expiration must be 5 characters")
    private String ccExpiration;

    @NotBlank (message = "Credit card CVV is required")
    @Size(min=3,max = 3, message = "Credit card CVV must be 3 characters")
    private String ccCVV; */

    @NotBlank(message = "The payment token is required")
    private String paymentToken;

    @NotNull(message= "Tacos are required")
    @Size (min=1, message = "You must order at least 1 taco")
    private List<Taco> tacos;

}
