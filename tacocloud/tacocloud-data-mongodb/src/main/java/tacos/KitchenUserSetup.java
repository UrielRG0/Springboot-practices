package tacos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tacos.data.UserRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

@Configuration
public class KitchenUserSetup {

    @Bean
    public CommandLineRunner createKitchenStaffAndStock(UserRepository userRepo, 
                                                        PasswordEncoder encoder,
                                                        ReactiveMongoTemplate mongoTemplate) {
        return args -> {
            userRepo.findByUsername("gordon")
                .switchIfEmpty(
                    userRepo.save(new User(
                        "gordon", encoder.encode("pass"),
                        "Gordon", "Ramsay", "Kitchen", "Hell", "123", "555", "gordon@taco.com",
                        "ROLE_KITCHEN" 
                    ))
                ).subscribe(); 

            // Inicializamos stock masivo para pruebas para que nunca falte inventario
            String[] commonIngredients = {"FLTO", "CORN", "BEEF", "CARN", "CHED", "JACK", "TMTO", "LETC", "SLSA", "GCAM"};
            
            for (String ingId : commonIngredients) {
                mongoTemplate.exists(Query.query(Criteria.where("ingredientId").is(ingId)), "inventory")
                    .flatMap(exists -> {
                        if (!exists) {
                            org.springframework.data.mongodb.core.DocumentCallbackHandler doc = null;
                            org.bson.Document stockDoc = new org.bson.Document("ingredientId", ingId)
                                .append("quantity", 1000); 
                            return mongoTemplate.save(stockDoc, "inventory");
                        }
                        return reactor.core.publisher.Mono.empty();
                    }).subscribe();
            }
        };
    }
}