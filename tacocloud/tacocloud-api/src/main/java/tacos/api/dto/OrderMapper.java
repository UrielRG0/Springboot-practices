package tacos.api.dto;

import tacos.TacoOrder;
import java.util.stream.Collectors;

public class OrderMapper {
    
    public static TacoOrder toDomainOrder(OrderCreateRequest request) {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName(request.getDeliveryName());
        order.setDeliveryStreet(request.getDeliveryStreet());
        order.setDeliveryCity(request.getDeliveryCity());
        order.setDeliveryState(request.getDeliveryState());
        order.setDeliveryZip(request.getDeliveryZip());
        
        order.setItems(request.getItems());
        
        return order;
    }

    public static OrderResponse toResponse(TacoOrder order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setPlacedAt(order.getPlacedAt());
        response.setDeliveryName(order.getDeliveryName());

        if (order.getItems() != null) {
            response.setTacos(order.getItems().stream()
                .map(item -> item.getTaco().getName())
                .collect(Collectors.toList()));
        }
        
        return response;
    }

}