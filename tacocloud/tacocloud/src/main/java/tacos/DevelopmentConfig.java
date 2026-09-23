package tacos;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;
import tacos.data.PaymentMethodRepository;
import tacos.data.TacoRepository;
import tacos.data.UserRepository;

@Profile("!prod")
@Configuration
public class DevelopmentConfig {

  @Bean
  public CommandLineRunner dataLoader(IngredientRepository repo,
        UserRepository userRepo, PasswordEncoder encoder, TacoRepository tacoRepo,
        PaymentMethodRepository paymentMethodRepo) { 
    
    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {
        // Ingredientes inicializados con Precio ($), Stock Actual y Nivel de Reorden
        Ingredient flourTortilla = saveAnIngredient("FLTO", "Flour Tortilla", Type.WRAP, "5.00", 100, 20);
        Ingredient cornTortilla = saveAnIngredient("COTO", "Corn Tortilla", Type.WRAP, "4.50", 150, 30);
        Ingredient groundBeef = saveAnIngredient("GRBF", "Ground Beef", Type.PROTEIN, "15.00", 50, 10);
        Ingredient carnitas = saveAnIngredient("CARN", "Carnitas", Type.PROTEIN, "18.00", 40, 10);
        Ingredient tomatoes = saveAnIngredient("TMTO", "Diced Tomatoes", Type.VEGGIES, "8.00", 80, 15);
        Ingredient lettuce = saveAnIngredient("LETC", "Lettuce", Type.VEGGIES, "6.50", 90, 15);
        Ingredient cheddar = saveAnIngredient("CHED", "Cheddar", Type.CHEESE, "12.00", 60, 10);
        Ingredient jack = saveAnIngredient("JACK", "Monterrey Jack", Type.CHEESE, "14.00", 60, 10);
        Ingredient salsa = saveAnIngredient("SLSA", "Salsa", Type.SAUCE, "5.00", 120, 20);
        Ingredient sourCream = saveAnIngredient("SRCR", "Sour Cream", Type.SAUCE, "7.00", 100, 20);
        
        userRepo.deleteAll().block();
        User miUsuario = userRepo.save(new User("habuma", encoder.encode("password"), 
              "Craig Walls", "123 North Street", "Cross Roads", "TX", 
              "76227", "123-123-1234", "craig@habuma.com", "ROLE_ADMIN")).block();
          
        if (miUsuario != null) {
            paymentMethodRepo.save(new PaymentMethod(null, miUsuario, "tok_fake_98765", "VISA", "1111")).block();
        }       
        
        Taco taco1 = new Taco();
        taco1.setId("TACO1");
        taco1.setName("Carnivore");
        taco1.setIngredients(Arrays.asList(flourTortilla, groundBeef, carnitas, sourCream, salsa, cheddar));
        tacoRepo.save(taco1).subscribe();

        Taco taco2 = new Taco();
        taco2.setId("TACO2");
        taco2.setName("Bovine Bounty");
        taco2.setIngredients(Arrays.asList(cornTortilla, groundBeef, cheddar, jack, sourCream));
        tacoRepo.save(taco2).subscribe();

        Taco taco3 = new Taco();
        taco3.setId("TACO3");
        taco3.setName("Veg-Out");
        taco3.setIngredients(Arrays.asList(flourTortilla, cornTortilla, tomatoes, lettuce, salsa));
        tacoRepo.save(taco3).subscribe();

      }

      // recibe los datos de negocio y los setea en el ingrediente
      private Ingredient saveAnIngredient(String id, String name, Type type, String price, int stock, int reorder) {
        Ingredient ingredient = new Ingredient(id, name, type);
        ingredient.setUnitPrice(new BigDecimal(price));
        ingredient.setStockOnHand(stock);
        ingredient.setReorderLevel(reorder);
        repo.save(ingredient).subscribe();
        return ingredient;
      }
    };
  }
}