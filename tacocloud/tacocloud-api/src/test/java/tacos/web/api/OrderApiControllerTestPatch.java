package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.test.StepVerifier;
import tacos.TacoOrder;
import tacos.data.OrderRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class OrderApiControllerTestPatch {

    @SpringBootApplication(scanBasePackages = "tacos")
    @EnableReactiveMongoRepositories(basePackages = "tacos.data")
    static class TestApplication {
    }
    @Autowired
    private WebTestClient testClient;

    @Autowired
    private OrderRepository repo;

    @Test
    public void testPatchZipRegression() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryState("TX");
        order.setDeliveryZip("00000");
        TacoOrder saved = repo.save(order).block(); 

        whiteListOrderApiController patch = new whiteListOrderApiController();
        patch.setDeliveryZip("99999");

        testClient.patch().uri("/api/orders/" + saved.getId()).contentType(MediaType.APPLICATION_JSON).bodyValue(patch).exchange().expectStatus().isOk()
            .expectBody().jsonPath("$.deliveryZip").isEqualTo("99999").jsonPath("$.deliveryState").isEqualTo("TX");
    }

    @Test
    public void testPatchIgnoraCamposProhibidos() {
        TacoOrder order = new TacoOrder();
        order.setCcNumber("12341234");
        TacoOrder saved = repo.save(order).block();

        String jsonAtaque = "{\"deliveryCity\":\"Ags\", \"ccNumber\":\"HA\"}";

        testClient.patch().uri("/api/orders/" + saved.getId()).contentType(MediaType.APPLICATION_JSON).bodyValue(jsonAtaque).exchange().expectStatus().isOk();

        StepVerifier.create(repo.findById(saved.getId())).expectNextMatches(o -> 
                o.getCcNumber().equals("12341234") && 
                o.getDeliveryCity().equals("Ags")
            ).verifyComplete();
    }

    @Test
    public void testPatchOrderNotFound() {
        whiteListOrderApiController patch = new whiteListOrderApiController();
        patch.setDeliveryCity("Ags");

        testClient.patch().uri("/api/orders/FAKE_ID").contentType(MediaType.APPLICATION_JSON).bodyValue(patch).exchange().expectStatus().isNotFound();//404
    }
}