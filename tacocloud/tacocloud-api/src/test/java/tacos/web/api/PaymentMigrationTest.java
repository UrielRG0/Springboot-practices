package tacos.web.api;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(
    classes = PaymentMigrationTest.MigrationTestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
public class PaymentMigrationTest {
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class MigrationTestConfig {
    }

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    public void testMigration_RemovesLegacyPaymentFields() {
        Document legacyOrder = new Document();
        legacyOrder.put("_id", "OLD_ORDER_123");
        legacyOrder.put("deliveryName", "Usuario Viejo");
        legacyOrder.put("ccNumber", "4532000011112222"); 
        legacyOrder.put("ccCVV", "123");               
        legacyOrder.put("ccExpiration", "12/25");

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("ccNumber").exists(true),Criteria.where("ccCVV").exists(true)));

        Update update = new Update().unset("ccNumber").unset("ccCVV").unset("ccExpiration");

        Mono<Document> migrationProcess = mongoTemplate.save(legacyOrder, "tacoOrder") .then(mongoTemplate.updateMulti(query, update, "tacoOrder")).then(mongoTemplate.findById("OLD_ORDER_123", Document.class, "tacoOrder")); 

        StepVerifier.create(migrationProcess)
            .assertNext(cleanedOrder -> {
                assertNull(cleanedOrder.get("ccNumber"), "ccNumber doesnt eliminated");
                assertNull(cleanedOrder.get("ccCVV"), "cvv survives the migration");
                assertNull(cleanedOrder.get("ccExpiration"));
            })
            .verifyComplete();
    }
}