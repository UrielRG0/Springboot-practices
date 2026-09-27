package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SecurityMatrixTest.SecurityTestConfig.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
public class SecurityMatrixTest {
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class SecurityTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    // Unknow person can see the catalog without been blocked
    @Test
    public void testAnonymous_CanGetIngredients() throws Exception {
        mockMvc.perform(get("/api/ingredients"))
               .andExpect(result -> {
                   int status = result.getResponse().getStatus();
                   assert status != 401 && status != 403; // Validation 
               });
    }

    //Unknown person cant create orders 401 error
    @Test
    public void testAnonymous_CannotPostOrder() throws Exception {
        mockMvc.perform(post("/api/orders").contentType("application/json").content("{}"))
               .andExpect(status().isUnauthorized()); // 401
    }

    //  User cant administrate the infredients 403 error
    @Test
    @WithMockUser(roles = "USER")
    public void testUser_CannotPostIngredients() throws Exception {
        mockMvc.perform(post("/api/ingredients").contentType("application/json").content("{}"))
               .andExpect(status().isForbidden()); // 403
    }

    // Admin can administrate ingredient
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdmin_CanPostIngredients() throws Exception {
        mockMvc.perform(post("/api/ingredients").contentType("application/json").content("{}"))
               .andExpect(result -> {
                   int status = result.getResponse().getStatus();
                   assert status != 401 && status != 403;
               });
    }

    // Actuator Health its público
    @Test
    public void testAnonymous_CanAccessHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
               .andExpect(result -> {
                   int status = result.getResponse().getStatus();
                   assert status != 401 && status != 403;
               });
    }

    // Actuator general requieres ADMIN 
    @Test
    @WithMockUser(roles = "USER")
    public void testUser_CannotAccessActuator() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
               .andExpect(status().isForbidden()); // 403
    }

    //  /data-api denyAll
    @Test
    @WithMockUser(roles = "ADMIN") 
    public void testAdmin_CannotAccessDataApi() throws Exception {
        mockMvc.perform(get("/data-api/users"))
               .andExpect(status().isForbidden()); // 403
    }
    
    // deny-by-default
    @Test
    public void testAnonymous_CannotAccessUnknownRoute() throws Exception {
        mockMvc.perform(get("/api/ruta-nueva-inventada"))
               .andExpect(status().isUnauthorized()); // 401
    }
}