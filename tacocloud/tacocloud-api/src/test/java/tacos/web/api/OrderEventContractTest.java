package tacos.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;
import tacos.messaging.contract.OrderEventPayload;
import tacos.messaging.contract.OrderEventType;
import tacos.messaging.contract.OrderEvent;
import tacos.messaging.contract.OrderEventPayload;
import tacos.messaging.contract.OrderEventType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderEventContractTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization_ExcludesSensitiveData() throws Exception {
        User secretUser = new User("batman", "SUPER_SECRET_PASSWORD", "Bruce", "Cave", "Gotham", "NY", "123", "555", "b@dc.com", "ROLE_USER");
        TacoOrder domainOrder = new TacoOrder();
        domainOrder.setId("ORD_999");
        domainOrder.setUser(secretUser);
        domainOrder.setStatus(OrderStatus.CREATED);
        domainOrder.setDeliveryName("Bruce Wayne");

        OrderEventPayload payload = OrderEventPayload.fromDomain(domainOrder);
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, "CORR_999", payload);

        String json = mapper.writeValueAsString(event);

        assertTrue(json.contains("\"eventId\""));
        assertTrue(json.contains("\"correlationId\":\"CORR_999\""));
        assertTrue(json.contains("\"version\":\"v1\""));

        assertFalse(json.contains("SUPER_SECRET_PASSWORD"), "El JSON filtró la contraseña");
        assertFalse(json.contains("batman"), "El JSON filtró el nombre de usuario");
        assertFalse(json.contains("ROLE_USER"), "El JSON filtró el rol de seguridad");
        
        assertTrue(json.contains("\"deliveryName\":\"Bruce Wayne\""));
    }

    @Test
    public void testForwardCompatibility_IgnoresUnknownFields() throws Exception {
        String futureJsonEvent = "{"
                + "\"eventId\":\"" + UUID.randomUUID().toString() + "\","
                + "\"eventType\":\"ORDER_CREATED\","
                + "\"version\":\"v2\","
                + "\"correlationId\":\"12345\","
                + "\"newFutureField\":\"Este campo rompería un parser estricto\"," 
                + "\"payload\":{\"orderId\":\"12345\",\"status\":\"CREATED\"}"
                + "}";

        OrderEvent parsedEvent = mapper.readValue(futureJsonEvent, OrderEvent.class);

        assertNotNull(parsedEvent);
        assertEquals(OrderEventType.ORDER_CREATED, parsedEvent.getEventType());
        assertEquals("12345", parsedEvent.getPayload().getOrderId());
    }
}