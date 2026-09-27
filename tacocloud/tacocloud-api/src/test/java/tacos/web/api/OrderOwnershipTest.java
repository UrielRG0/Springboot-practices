package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.TacoOrder;
import tacos.User;
import tacos.InventoryService;
import tacos.web.api.PaymentGateway;
import tacos.data.OrderRepository;
import tacos.messaging.OrderMessagingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderOwnershipTest {

    //Ownership test
    @Test
    public void testUserACannotDeleteUserBOrder() {
        OrderRepository repoMock = mock(OrderRepository.class);
        OrderMessagingService messagingMock = mock(OrderMessagingService.class);
        EmailOrderService emailMock = mock(EmailOrderService.class);
        PaymentGateway paymentMock = mock(PaymentGateway.class);
        OrderPricingService pricingMock = mock(OrderPricingService.class);
        InventoryService inventoryMock = mock(InventoryService.class);
        
        OrderApiController controller = new OrderApiController(repoMock, messagingMock, emailMock, paymentMock, pricingMock, inventoryMock);
        
        User userA = new User("userA", "pass", "A", "A", "A", "A", "A", "A", "a@a.com", "ROLE_USER");
        userA.setId("ID_USER_A"); 
        User userB = new User("userB", "pass", "B", "B", "B", "B", "B", "B", "b@b.com", "ROLE_USER");
        userB.setId("ID_USER_B");
        TacoOrder orderOfUserB = new TacoOrder();
        orderOfUserB.setId("ORDER_1");
        orderOfUserB.setUser(userB);

        when(repoMock.findById("ORDER_1")).thenReturn(Mono.just(orderOfUserB));
        Mono<ResponseEntity<Void>> result = controller.deleteOrder("ORDER_1", userA);
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "The system doesnt block a user for order of other one");
            })
            .verifyComplete();
    }
}