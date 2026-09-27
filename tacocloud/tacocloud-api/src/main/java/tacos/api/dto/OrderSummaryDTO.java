package tacos.api.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderSummaryDTO {
    private String id;
    private Date placedAt;
    private String status;
    private BigDecimal total;
    private String deliveryName;
}