package tacos.api.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;
import tacos.OrderItem; 

@Data
public class KitchenOrderDTO {
    private String id;
    private Date placedAt;
    private String status;
    private String cookId;
    private Integer estimatedPrepMinutes;
    
    private List<OrderItem> items; 
}