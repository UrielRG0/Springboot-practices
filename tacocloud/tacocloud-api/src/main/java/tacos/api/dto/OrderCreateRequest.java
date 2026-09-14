package tacos.api.dto;

import lombok.Data;
import java.util.List;
import tacos.Taco;

@Data
public class OrderCreateRequest {
    
    private String deliveryName;
    private String deliveryStreet;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZip;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;
    private List<Taco> tacos;

}
