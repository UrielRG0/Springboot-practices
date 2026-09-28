package tacos;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class OrderAuditLog {
    private Date timestamp = new Date();
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private String username;
    private String reason;

    public OrderAuditLog(OrderStatus from, OrderStatus to, String user, String reason) {
        this.fromStatus = from;
        this.toStatus = to;
        this.username = user;
        this.reason = reason;
    }
}