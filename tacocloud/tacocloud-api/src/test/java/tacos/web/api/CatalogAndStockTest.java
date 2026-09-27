package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.Ingredient;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = CatalogAndStockTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
public class CatalogAndStockTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    @Test
    public void testIngredient_UsesBigDecimal_NotDouble() {
        Field[] fields = Ingredient.class.getDeclaredFields();
        
        boolean hasUnitPrice = false;
        boolean hasVersion = false;

        for (Field field : fields) {
            if (field.getName().equals("unitPrice")) {
                assertEquals(BigDecimal.class, field.getType(), "The price must be big decimal");
                hasUnitPrice = true;
            }
            if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                fail("double doesnt have been here" + field.getName());
            }
            if (field.isAnnotationPresent(Version.class)) {
                hasVersion = true;
            }
        }
        
        assertTrue(hasUnitPrice, "unitPrice must be in ingredients");
        assertTrue(hasVersion, "@version must be in ingredients");
    }
    @Test
    public void testIngredient_OptimisticLocking_ThrowsExceptionOnConflict() {
        Ingredient ing = new Ingredient("TEST_OPT", "Ingrediente Concurrente", Ingredient.Type.VEGGIES);
        ing.setAvailable(true);
        ing.setStockOnHand(100);
        
        Ingredient savedOriginal = mongoTemplate.save(ing).block();
        assertNotNull(savedOriginal.getVersion(), "The version not generated in MongoDB");

        Ingredient admin1 = mongoTemplate.findById(savedOriginal.getId(), Ingredient.class).block();
        Ingredient admin2 = mongoTemplate.findById(savedOriginal.getId(), Ingredient.class).block();

        admin1.setStockOnHand(90);
        mongoTemplate.save(admin1).block();

        admin2.setStockOnHand(80);
        StepVerifier.create(mongoTemplate.save(admin2))
            .expectErrorMatches(throwable -> throwable instanceof OptimisticLockingFailureException || throwable.getMessage().toLowerCase().contains("optimistic")).verify();
    }
}