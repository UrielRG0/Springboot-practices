package tacos.messaging.contract;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrderEventPayload {
    private String orderId;
    private String status;
    private String deliveryName;
    private int totalItems;
    private List<String> itemNames; 

    public static OrderEventPayload fromDomain(tacos.TacoOrder order) {
        OrderEventPayload payload = new OrderEventPayload();
        payload.setOrderId(order.getId());
        payload.setStatus(order.getStatus() != null ? order.getStatus().name() : "CREATED");
        payload.setDeliveryName(order.getDeliveryName());
        if (order.getItems() != null) {
            payload.setTotalItems(order.getItems().size());
            payload.setItemNames(order.getItems().stream()
                .map(item -> item.getTaco().getName())
                .collect(Collectors.toList()));
        } else {
            payload.setTotalItems(0);
        }
        return payload;
    }
}