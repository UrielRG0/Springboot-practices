package tacos.core;

import org.springframework.util.DigestUtils;
import tacos.TacoOrder;
import java.nio.charset.StandardCharsets;

public class CanonicalHasher {
    public static String hashOrder(TacoOrder order) {
        String raw = String.format("%s|%s|%d|%s", 
            order.getDeliveryName(),
            order.getDeliveryStreet(),
            order.getItems() != null ? order.getItems().size() : 0,
            order.getTotal() != null ? order.getTotal().toString() : "0"
        );
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}