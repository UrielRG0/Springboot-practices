package tacos.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.PaymentMethod;
import tacos.TacoOrder;
import tacos.User;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSecurityTest {

    //TacoORder doesnt have sensibility parts
    @Test
    public void testTacoOrder_HasNoSensitivePaymentFields() {
        Field[] fields = TacoOrder.class.getDeclaredFields();
        List<String> fieldNames = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
        assertFalse(fieldNames.contains("ccNumber"), "¡Peligro! ccNumber todavía existe en TacoOrder");
        assertFalse(fieldNames.contains("ccCVV"), "¡Peligro! ccCVV todavía existe en TacoOrder, violación a PCI-DSS");
        assertFalse(fieldNames.contains("ccExpiration"), "ccExpiration no debería estar en TacoOrder");
    }

    // Test adaptator of fake gateway
    @Test
    public void testFakePaymentGateway_TokenizesWithoutSavingCVV() {
        PaymentGateway fakeGateway = (tokenRecibido, user) -> {
            PaymentMethod safeMethod = new PaymentMethod();
            return Mono.just(safeMethod);
        };

        User testUser = mockUser();
        Mono<PaymentMethod> result = fakeGateway.tokenize("tok_visa_sintetico_123", testUser);

        StepVerifier.create(result)
            .assertNext(paymentMethod -> {
                assertNotNull(paymentMethod, "La pasarela debe devolver un método de pago");
                boolean hasCVV = Arrays.stream(paymentMethod.getClass().getDeclaredFields())
                                       .anyMatch(f -> f.getName().toLowerCase().contains("cvv"));
                assertFalse(hasCVV, "PaymentMethod no debe contener CVV. ¡Violación de seguridad!");
            })
            .verifyComplete();
    }

    // The kitchen event doesnt have paymenth information
    @Test
    public void testTacoOrder_Serialization_HasZeroPaymentInfo() throws Exception {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Craig Walls");
        order.setPlacedAt(new Date());

        ObjectMapper mapper = new ObjectMapper();
        String jsonOutput = mapper.writeValueAsString(order);
        assertFalse(jsonOutput.contains("ccNumber"), "The JSON clean the ccNumber");
        assertFalse(jsonOutput.contains("ccCVV"), "The json clean the CVV");
        assertFalse(jsonOutput.contains("paymentToken"), "The json clean the token paymenth in the kitchen"); 
        
        assertTrue(jsonOutput.contains("Craig Walls"), "The name must be in the JSON"); 
    }

    // Auxiliar method
    private User mockUser() {
        try {
            return new User("user", "pass", "A", "A", "A", "A", "A", "A", "a@a.com", "ROLE_USER");
        } catch (Exception e) {
            return null; 
        }
    }
}