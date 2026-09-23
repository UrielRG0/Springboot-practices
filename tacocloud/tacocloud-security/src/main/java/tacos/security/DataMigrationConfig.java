package tacos.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate; 
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult; 

import tacos.TacoOrder;
import tacos.PaymentMethod;

@Configuration
public class DataMigrationConfig {

    @Bean
    public CommandLineRunner cleanLegacyPaymentData(ReactiveMongoTemplate mongoTemplate) { 
        return args -> {
            Query query = new Query(Criteria.where("ccNumber").exists(true));
            Update update = new Update().unset("ccNumber").unset("ccExpiration").unset("ccCVV");

            UpdateResult ordersResult = mongoTemplate.updateMulti(query, update, TacoOrder.class).block();
            UpdateResult paymentsResult = mongoTemplate.updateMulti(query, update, PaymentMethod.class).block();
            
            long ordersUpdated = ordersResult != null ? ordersResult.getModifiedCount() : 0;
            long paymentsUpdated = paymentsResult != null ? paymentsResult.getModifiedCount() : 0;
        };
    }
}