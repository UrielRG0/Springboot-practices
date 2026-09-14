package tacos.api.dto;

import lombok.Data;
import java.util.Date;
//import tacos.Taco;
import java.util.List;

@Data
public class OrderResponse {
    
    private String id;
    private Date placedAt;
    private String status;
    private String deliveryName;
    private List<String> tacos;



}
