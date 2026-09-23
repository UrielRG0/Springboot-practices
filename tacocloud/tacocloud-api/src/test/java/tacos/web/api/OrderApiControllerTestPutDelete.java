package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import tacos.TacoOrder;
import tacos.User;
import tacos.InventoryService;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;
import tacos.api.dto.OrderCreateRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class OrderApiControllerTestPutDelete {

    @SpringBootApplication(scanBasePackages = "tacos")
    @EnableReactiveMongoRepositories(basePackages = "tacos.data")
    static class TestApplication { }
    
    @Autowired private WebTestClient testClient;
    @Autowired private OrderRepository repo;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    @MockBean private OrderPricingService pricingService;
    @MockBean private InventoryService inventoryService;

    private User propietario;
    private User intruso;

    @BeforeEach
    public void setupDb() {
        propietario = userRepo.findByUsername("propietario").block();
        if (propietario == null) {
            propietario = new User("propietario", encoder.encode("password"), "Propietario", "Calle", "City", "ST", "ZIP", "555", "prop@test.com", "ROLE_USER");
            propietario = userRepo.save(propietario).block();
        }

        intruso = userRepo.findByUsername("intruso").block();
        if (intruso == null) {
            intruso = new User("intruso", encoder.encode("password"), "Intruso", "Calle", "City", "ST", "ZIP", "555", "int@test.com", "ROLE_USER");
            intruso = userRepo.save(intruso).block();
        }

        when(pricingService.calculatePrices(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(inventoryService.releaseInventory(any())).thenReturn(Mono.empty());
    }

    @Test
    public void testDeleteOrderSuccess() {
        TacoOrder order = new TacoOrder();
        order.setStatus("PLACED");
        order.setUser(propietario);
        TacoOrder saved = repo.save(order).block();
        testClient.delete().uri("/api/orders/" + saved.getId()).headers(h -> h.setBasicAuth("propietario", "password")).exchange().expectStatus().isNoContent();
    }

    @Test
    public void testDeleteOrderForbidden_Ajeno() {
        TacoOrder order = new TacoOrder();
        order.setUser(propietario);
        TacoOrder saved = repo.save(order).block();
        testClient.delete().uri("/api/orders/" + saved.getId()).headers(h -> h.setBasicAuth("intruso", "password")).exchange().expectStatus().isForbidden();
    }

    @Test
    public void testDeleteOrderConflict_Preparing() {
        TacoOrder order = new TacoOrder();
        order.setStatus("PREPARING"); 
        order.setUser(propietario);
        TacoOrder saved = repo.save(order).block();
        testClient.delete().uri("/api/orders/" + saved.getId()).headers(h -> h.setBasicAuth("propietario", "password")).exchange().expectStatus().isEqualTo(409); 
    }

    @Test
    public void testDeleteOrderNotFound() {
        testClient.delete().uri("/api/orders/ID_FALSO_123")
            .headers(h -> h.setBasicAuth("propietario", "password"))
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void testPutOrderSuccess() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryCity("Original City");
        order.setUser(propietario);
        TacoOrder saved = repo.save(order).block();
        String jsonPutRequest = "{\n" +
            "  \"deliveryName\": \"Juan Perez\",\n" +
            "  \"deliveryStreet\": \"Av Siempre Viva 123\",\n" +
            "  \"deliveryCity\": \"Ciudad Actualizada\",\n" +
            "  \"deliveryState\": \"TX\",\n" +
            "  \"deliveryZip\": \"12345\",\n" +
            "  \"paymentToken\": \"tok_123456\",\n" +
            "  \"items\": [\n" +
            "    {\n" +
            "      \"quantity\": 1,\n" +
            "      \"taco\": {\n" +
            "        \"name\": \"Taco Test\",\n" +
            "        \"ingredients\": [\n" +
            "          { \"id\": \"FLTO\" }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        testClient.put().uri("/api/orders/" + saved.getId())
            .headers(h -> h.setBasicAuth("propietario", "password"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonPutRequest) 
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.deliveryCity").isEqualTo("Ciudad Actualizada");
    }
}