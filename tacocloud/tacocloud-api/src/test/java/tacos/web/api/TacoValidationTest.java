package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Flux;
import tacos.validation.TacoValidatorService;
import tacos.validation.ValidationViolation;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TacoValidationTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class TacoValidationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TacoValidatorService validatorService;

    //Valid design
    @Test
    public void testValidTacoDesign_PassesWithoutViolations() throws Exception {
        when(validatorService.validate(any())).thenReturn(Flux.empty());

        String validTacoJson = "{ \"name\": \"Taco Perfecto\", \"ingredients\": [] }";
        MvcResult result = mockMvc.perform(post("/api/tacos/validate").contentType(MediaType.APPLICATION_JSON).content(validTacoJson)).andReturn();
        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    // Invalid Design
    @Test
    public void testInvalidTacoDesign_AccumulatesMultipleViolations() throws Exception {
        when(validatorService.validate(any())).thenReturn(Flux.just(
            new ValidationViolation("MISSING_BASE", "The taco need a tortilla or in case a bowl"),
            new ValidationViolation("TOO_FEW_INGREDIENTS", "you need 2 ingredient minimum")
        ));

        String invalidTacoJson = "{ \"name\": \"Taco Roto\", \"ingredients\": [] }";

        MvcResult result = mockMvc.perform(post("/api/tacos/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTacoJson))
            .andReturn();

        mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].code", is("MISSING_BASE")));
    }

    // ingredient duplicateed
    @Test
    public void testDuplicateIngredients_TriggersViolation() throws Exception {
        when(validatorService.validate(any())).thenReturn(Flux.just(
            new ValidationViolation("DUPLICATE_INGREDIENT", "You cant duplicate ingridients")
        ));

        String duplicateTacoJson = "{ \"name\": \"Taco Duplicado\", \"ingredients\": [] }";

        MvcResult result = mockMvc.perform(post("/api/tacos/validate").contentType(MediaType.APPLICATION_JSON).content(duplicateTacoJson)).andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isBadRequest()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].code", is("DUPLICATE_INGREDIENT")));
    }
}