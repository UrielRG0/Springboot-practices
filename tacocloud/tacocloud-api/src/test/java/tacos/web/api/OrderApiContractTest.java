package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.InventoryService;
import tacos.OrderWorkflowService;
import tacos.core.OrderPlacementService;
import tacos.data.OrderRepository;
import tacos.messaging.contract.OrderMessagingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderApiContractTest {

    private WebTestClient webClient;
    private OrderPlacementService placementMock;
    private PaymentGateway paymentMock;
    private OrderPricingService pricingMock;
    private InventoryService inventoryMock;

    @BeforeEach
    public void setup() {
        OrderRepository repoMock = mock(OrderRepository.class);
        OrderMessagingService messagingMock = mock(OrderMessagingService.class);
        EmailOrderService emailMock = mock(EmailOrderService.class);
        paymentMock = mock(PaymentGateway.class);
        pricingMock = mock(OrderPricingService.class);
        inventoryMock = mock(InventoryService.class);
        OrderWorkflowService workflowMock = mock(OrderWorkflowService.class);
        placementMock = mock(OrderPlacementService.class);

        OrderApiController controller = new OrderApiController(
                repoMock, messagingMock, emailMock, paymentMock,
                pricingMock, inventoryMock, workflowMock, placementMock
        );

        webClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void testDeprecationAlias_BothUrlsWork() {
        String payload = "{\"deliveryName\":\"Tony Stark\", \"deliveryStreet\":\"10880 Malibu Point\", \"deliveryCity\":\"Malibu\", \"deliveryState\":\"CA\", \"deliveryZip\":\"90265\", \"paymentToken\":\"tok_123\", \"items\":[{\"quantity\":1, \"taco\": {\"name\":\"Taco Loco\", \"ingredients\":[]}}]}";

        TacoOrder mockOrder = new TacoOrder();
        mockOrder.setId("orden-alias-1");
        mockOrder.setDeliveryName("Tony Stark");

        when(paymentMock.tokenize(any(), any())).thenReturn(Mono.just(mock(tacos.PaymentMethod.class)));
        when(pricingMock.calculatePrices(any())).thenReturn(Mono.just(mockOrder));
        when(inventoryMock.reserveInventory(any())).thenReturn(Mono.empty());
        when(placementMock.placeOrderTransactionally(any(), any(), any())).thenReturn(Mono.just(mockOrder));

        webClient.post().uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("orden-alias-1");

        webClient.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("orden-alias-1");
    }

    @Test
    public void testContract_NoSensitiveFieldsExposed() {
        String payload = "{\"deliveryName\":\"Bruce Wayne\", \"deliveryStreet\":\"Wayne Manor\", \"deliveryCity\":\"Gotham\", \"deliveryState\":\"NJ\", \"deliveryZip\":\"07014\", \"paymentToken\":\"tok_secreto_123\", \"items\":[{\"quantity\":1, \"taco\": {\"name\":\"Bat Taco\", \"ingredients\":[]}}]}";

        TacoOrder mockOrder = new TacoOrder();
        mockOrder.setId("orden-segura");
        mockOrder.setDeliveryName("Bruce Wayne");

        when(paymentMock.tokenize(any(), any())).thenReturn(Mono.just(mock(tacos.PaymentMethod.class)));
        when(pricingMock.calculatePrices(any())).thenReturn(Mono.just(mockOrder));
        when(inventoryMock.reserveInventory(any())).thenReturn(Mono.empty());
        when(placementMock.placeOrderTransactionally(any(), any(), any())).thenReturn(Mono.just(mockOrder));

        webClient.post().uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.deliveryName").isEqualTo("Bruce Wayne")
                .jsonPath("$.paymentToken").doesNotExist()
                .jsonPath("$.user").doesNotExist()
                .jsonPath("$.password").doesNotExist();
    }
}