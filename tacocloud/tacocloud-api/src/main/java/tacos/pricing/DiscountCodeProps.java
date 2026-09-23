package tacos.pricing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "tacos.discount")
public class DiscountCodeProps {
    
    private Map<String, CouponConfig> codes = new HashMap<>();

    @Data
    public static class CouponConfig {
        private String type; 
        private BigDecimal value;
        private BigDecimal minPurchase = BigDecimal.ZERO;
        private BigDecimal maxDiscount;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }
}