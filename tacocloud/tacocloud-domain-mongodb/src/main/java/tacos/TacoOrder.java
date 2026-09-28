package tacos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class TacoOrder implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private String id;
  private Date placedAt = new Date();

  private User user;

  private String deliveryName;
  private String deliveryStreet;
  private String deliveryCity;
  private String deliveryState;
  private String deliveryZip;

  private String discountCode;
  private BigDecimal discountAmount = BigDecimal.ZERO;

  private PaymentMethod paymentMethod;

  private OrderStatus status = OrderStatus.CREATED;

  private java.util.List<OrderAuditLog> statusHistory = new java.util.ArrayList<>();

  @org.springframework.data.annotation.Version
  private Long version;

  private List<OrderItem> items = new ArrayList<>();

  private BigDecimal total = BigDecimal.ZERO;

  private String cookId;
  private Integer estimatedPrepMinutes;

  public void addOrderItem(OrderItem item) {
    this.items.add(item);
  }
}