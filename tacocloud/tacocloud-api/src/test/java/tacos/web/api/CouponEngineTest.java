package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
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
import tacos.pricing.DiscountCodeProps;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = CouponEngineTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class CouponEngineTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "tacos")
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Clock clock; 
    @MockBean
    private DiscountCodeProps couponProps;

    @BeforeEach
    public void setupMocks() {
        Map<String, DiscountCodeProps.CouponConfig> mockMap = new HashMap<>();

        // 50% descount
        DiscountCodeProps.CouponConfig fix50 = mock(DiscountCodeProps.CouponConfig.class);
        when(fix50.getType()).thenReturn("FIXED");
        when(fix50.getValue()).thenReturn(new BigDecimal("50.00"));
        mockMap.put("FIX50", fix50);

        // Expire un 2026
        DiscountCodeProps.CouponConfig taco2026 = mock(DiscountCodeProps.CouponConfig.class);
        when(taco2026.getType()).thenReturn("FIXED");
        when(taco2026.getValue()).thenReturn(new BigDecimal("10.00"));
        when(taco2026.getEndDate()).thenReturn(LocalDate.of(2026, 12, 31));
        mockMap.put("TACO2026", taco2026);

        // NOrmal cupon 20 bucks
        DiscountCodeProps.CouponConfig taco20 = mock(DiscountCodeProps.CouponConfig.class);
        when(taco20.getType()).thenReturn("FIXED");
        when(taco20.getValue()).thenReturn(new BigDecimal("20.00"));
        mockMap.put("TACO20", taco20);

        when(couponProps.getCodes()).thenReturn(mockMap);
    }

    //Never let the discount get the price in negative numbers
    @Test
    public void testCoupon_NeverMakesTotalNegative() throws Exception {
        when(clock.instant()).thenReturn(Instant.parse("2026-09-26T12:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        String quoteRequest = "{ \"subtotal\": 10.00, \"code\": \"FIX50\" }";

        mockMvc.perform(post("/api/coupons/validate") .contentType(MediaType.APPLICATION_JSON).content(quoteRequest)).andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.discount").value(10.00)); 
    }

    // Expired cupon 
    @Test
    public void testCoupon_Expired_WithClockManipulation() throws Exception {
        when(clock.instant()).thenReturn(Instant.parse("2030-01-01T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        String quoteRequest = "{ \"subtotal\": 100.00, \"code\": \"TACO2026\" }";

        mockMvc.perform(post("/api/coupons/validate").contentType(MediaType.APPLICATION_JSON).content(quoteRequest)).andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.discount").value(0.00)); 
    }

    // Security test
    @Test
    public void testCoupon_DoesNotEnumerateOrListCodes() throws Exception {
        mockMvc.perform(get("/api/coupons"))
            .andExpect(status().is4xxClientError());
    }
    
    // Mayu and Min equals the same 
    @Test
    public void testCoupon_CaseInsensitive() throws Exception {
        when(clock.instant()).thenReturn(Instant.parse("2026-09-26T12:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        String quoteRequest = "{ \"subtotal\": 100.00, \"code\": \"tAcO20\" }";

        mockMvc.perform(post("/api/coupons/validate").contentType(MediaType.APPLICATION_JSON).content(quoteRequest)).andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.discount").value(20.00));
    }
}