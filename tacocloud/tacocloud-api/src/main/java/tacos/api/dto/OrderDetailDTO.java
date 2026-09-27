package tacos.api.dto;
import lombok.Data;
import tacos.OrderItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetailDTO {
    private String id;
    private Date placedAt;
    private String status;
    private BigDecimal total;
    private String deliveryName;
    private String deliveryStreet;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZip;
    private List<OrderItem> items;
}