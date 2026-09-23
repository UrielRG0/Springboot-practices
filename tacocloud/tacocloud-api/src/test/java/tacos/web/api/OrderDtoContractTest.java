package tacos.web.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tacos.api.dto.OrderCreateRequest;
import tacos.api.dto.OrderResponse;

import tacos.TacoOrder;

public class OrderDtoContractTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void testOrderResponse_DoesNotLeakSensitiveData() throws Exception {
        OrderResponse response = new OrderResponse(); 
        response.setId("mongo-id-interno");
        response.setDeliveryName("Craig Walls");

        String jsonOutput = objectMapper.writeValueAsString(response);

        assertFalse(jsonOutput.contains("password"), "JSON must not show the password");
        assertFalse(jsonOutput.contains("paymentToken"), "JSON must not show the token");
        assertFalse(jsonOutput.contains("authorities"), "JSON must not show the roles");
    }


    @Test
    public void testOrderCreateRequest_MapsCorrectlyWithoutServerOwnedFields() throws Exception {
        String validJson = "{\n" +
                "  \"deliveryName\": \"Craig Walls\",\n" +
                "  \"deliveryStreet\": \"123 Taco St\",\n" +
                "  \"deliveryCity\": \"Dallas\"\n" +
                "}";

        OrderCreateRequest request = objectMapper.readValue(validJson, OrderCreateRequest.class);

        assertEquals("Craig Walls", request.getDeliveryName());
        assertEquals("123 Taco St", request.getDeliveryStreet());
        assertEquals("Dallas", request.getDeliveryCity());
        assertThrows(NoSuchMethodException.class, () -> {
            OrderCreateRequest.class.getMethod("getId");
        }, "the request shouldnt had ID"); 
        
        assertThrows(NoSuchMethodException.class, () -> {
            OrderCreateRequest.class.getMethod("getPlacedAt");
        }, "The request shouldnt had placedAt"); 
    }

    @Test
    public void testMassAssignment_MaliciousFieldsAreIgnored() throws Exception {
        String maliciousJson = "{\n" +
                "  \"deliveryName\": \"Hacker\",\n" +
                "  \"_id\": \"60d5ec49f1\",\n" +       
                "  \"id\": \"admin_order_1\",\n" +    
                "  \"userId\": \"admin-user-id\",\n" + 
                "  \"placedAt\": \"2099-01-01\",\n" + 
                "  \"total\": 0.00\n" +               
                "}";

        OrderCreateRequest request = objectMapper.readValue(maliciousJson, OrderCreateRequest.class);
        assertEquals("Hacker", request.getDeliveryName());
        TacoOrder domainOrder = new TacoOrder();
        domainOrder.setDeliveryName(request.getDeliveryName());
        assertNull(domainOrder.getId(), "The Id shouldnt inject");
        assertNull(domainOrder.getUser(), "The user shouldnt assigned in the JSON"); 
    }
}