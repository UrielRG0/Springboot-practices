package tacos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tacos.data.UserRepository; 

@Configuration
public class KitchenUserSetup {

    @Bean
    public CommandLineRunner createKitchenStaff(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            userRepo.findByUsername("gordon")
                .switchIfEmpty(
                    userRepo.save(new User(
                        "gordon", encoder.encode("pass"),
                        "Gordon", "Ramsay", "Kitchen", "Hell", "123", "555", "gordon@taco.com",
                        "ROLE_KITCHEN" 
                    ))
                ).subscribe(); 
        };
    }
}