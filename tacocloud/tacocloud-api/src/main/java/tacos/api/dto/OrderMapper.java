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
        order.setCcNumber(request.getCcNumber());
        order.setCcExpiration(request.getCcExpiration());
        order.setCcCVV(request.getCcCVV());
        order.setTacos(request.getTacos());
        return order;
    }

    public static OrderResponse toResponse(TacoOrder order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setPlacedAt(order.getPlacedAt());
        response.setDeliveryName(order.getDeliveryName());
        if (order.getTacos() != null) {
            response.setTacos(order.getTacos().stream().map(taco -> taco.getName()).collect(Collectors.toList()));
        }
        return response;
    }

}
