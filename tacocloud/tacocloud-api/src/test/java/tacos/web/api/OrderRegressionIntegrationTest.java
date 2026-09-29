package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRegressionIntegrationTest extends IntegrationTestBase {

    @Autowired
    private WebTestClient webClient; 

    @Test
    public void securityRegression_UnauthenticatedUser_Gets401() {
        String payload = "{\"deliveryName\":\"Hacker\", \"paymentToken\":\"tok_123\", \"items\":[]}";

        webClient.post().uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void idempotencyRegression_DoubleRequest_CreatesOnlyOneOrder() {
        String payload = "{\"deliveryName\":\"Tony Stark\", \"deliveryStreet\":\"10880 Malibu Point\", \"deliveryCity\":\"Malibu\", \"deliveryState\":\"CA\", \"deliveryZip\":\"90265\", \"paymentToken\":\"tok_123\", \"items\":[{\"quantity\":1, \"taco\": {\"name\":\"Taco Loco\", \"ingredients\":[]}}]}";
        String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString("buzz:infinity".getBytes());


        String orderId = webClient.post().uri("/api/v1/orders")
                .header("Authorization", basicAuthHeader)
                .header("Idempotency-Key", "idempotency-integration-777")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(tacos.api.dto.OrderSummaryDTO.class)
                .getResponseBody().blockFirst().getId();

        assertThat(orderId).isNotNull();

        webClient.post().uri("/api/v1/orders")
                .header("Authorization", basicAuthHeader)
                .header("Idempotency-Key", "idempotency-integration-777")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated() 
                .expectBody()
                .jsonPath("$.id").isEqualTo(orderId); 
    }
}