package tacos.api.dto;

import lombok.Data;
import java.util.List;
import tacos.OrderItem; 
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

@Data
public class OrderCreateRequest {
    
    @NotBlank(message = "Delivery name is required")
    @Size(min=2, max = 50, message = "Delivery name must be between 2 and 50 characters")
    private String deliveryName;

    @NotBlank(message = "Delivery street is required")
    @Size(min=2, max = 50, message = "Delivery street must be between 2 and 50 characters")
    private String deliveryStreet;

    @NotBlank (message = "Delivery city is required")
    @Size(min=2, max = 50, message = "Delivery city must be between 2 and 50 characters")
    private String deliveryCity;

    @NotBlank (message = "Delivery state is required")
    @Size(min=2, max = 2, message = "Delivery state must be 2 characters")
    private String deliveryState;

    @NotBlank (message = "Delivery zip is required")
    @Size(min=4, max = 10, message = "Delivery zip must be between 4 and 10 characters")
    private String deliveryZip;

    @NotBlank(message = "The payment token is required")
    private String paymentToken;

    @NotNull(message= "Items are required")
    @Size(min = 1, message = "You must order at least 1 item")
    private List<OrderItem> items;

}