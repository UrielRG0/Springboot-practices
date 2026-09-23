package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.test.StepVerifier;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;
import tacos.api.dto.OrderPatchRequest;

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

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    public void setupDb() {
        if (userRepo.findByUsername("testadmin").block() == null) {
            User admin = new User("testadmin", encoder.encode("password"), 
                    "Jefe de Pruebas", "Calle", "Ciudad", "Estado", "ZIP", "555-5555", "admin@test.com", "ROLE_ADMIN");
            userRepo.save(admin).block();
        }
    }

    @Test
    public void testPatchZipRegression() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryState("TX");
        order.setDeliveryZip("00000");
        TacoOrder saved = repo.save(order).block(); 

        OrderPatchRequest patch = new OrderPatchRequest();
        patch.setDeliveryZip("99999");

        testClient.patch().uri("/api/orders/" + saved.getId()).headers(h -> h.setBasicAuth("testadmin", "password")).contentType(MediaType.APPLICATION_JSON).bodyValue(patch)
            .exchange().expectStatus().isOk().expectBody().jsonPath("$.deliveryZip").isEqualTo("99999").jsonPath("$.deliveryState").isEqualTo("TX"); 
    }

    //Verifica el uso de la anotación @JsonIgnore en los campos prohibidos (ccNumber, ccExpiration, ccCVV)
    @Test
    public void testPatchIgnoraCamposProhibidos() {
        TacoOrder order = new TacoOrder();
        TacoOrder saved = repo.save(order).block();

        String jsonAtaque = "{\"deliveryCity\":\"Ags\", \"ccNumber\":\"HA\"}";

        testClient.patch().uri("/api/orders/" + saved.getId()).headers(h -> h.setBasicAuth("testadmin", "password")).contentType(MediaType.APPLICATION_JSON).bodyValue(jsonAtaque)
            .exchange().expectStatus().isOk(); 

        StepVerifier.create(repo.findById(saved.getId())).expectNextMatches(o -> o.getDeliveryCity().equals("Ags")).verifyComplete();
    }

    //si hace un patch a un order que no existe, debe devolver 404
    @Test
    public void testPatchOrderNotFound() {
        OrderPatchRequest patch = new OrderPatchRequest();
        patch.setDeliveryCity("Ags");

        testClient.patch().uri("/api/orders/FAKE_ID").headers(h -> h.setBasicAuth("testadmin", "password")).contentType(MediaType.APPLICATION_JSON).bodyValue(patch).exchange()
            .expectStatus().isNotFound(); 
    }
}