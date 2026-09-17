package tacos.security;

import tacos.TacoOrder;
import tacos.PaymentMethod;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Configuration
public class DataMigrationConfig {

    @Bean
    public CommandLineRunner cleanLegacyPaymentData(MongoTemplate mongoTemplate) {
        return args -> {
            Query query = new Query(Criteria.where("ccNumber").exists(true));
            Update update = new Update().unset("ccNumber").unset("ccExpiration").unset("ccCVV");

            long ordersUpdated = mongoTemplate.updateMulti(query, update, TacoOrder.class).getModifiedCount();
            long paymentsUpdated = mongoTemplate.updateMulti(query, update, PaymentMethod.class).getModifiedCount();
        };
    }
}