package tacos.pricing;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;

@Service
public class CouponService {

    private final DiscountCodeProps couponProps;
    private final Clock clock;

    public CouponService(DiscountCodeProps couponProps, Clock clock) {
        this.couponProps = couponProps;
        this.clock = clock;
    }

    public BigDecimal calculateDiscount(String code, BigDecimal subtotal) {
        if (code == null || code.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String normalizedCode = code.trim().toUpperCase();
        DiscountCodeProps.CouponConfig config = couponProps.getCodes().get(normalizedCode);
        if (config == null) {
            return BigDecimal.ZERO;
        }
        LocalDate today = LocalDate.now(clock);

        // Validar vigencia por fechas
        if (config.getStartDate() != null && today.isBefore(config.getStartDate())) {
            return BigDecimal.ZERO;
        }
        if (config.getEndDate() != null && today.isAfter(config.getEndDate())) {
            return BigDecimal.ZERO; 
        }

        if (config.getMinPurchase() != null && subtotal.compareTo(config.getMinPurchase()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        if ("PERCENTAGE".equalsIgnoreCase(config.getType())) {
            discount = subtotal.multiply(config.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (config.getMaxDiscount() != null && discount.compareTo(config.getMaxDiscount()) > 0) {
                discount = config.getMaxDiscount();
            }
        } else if ("FIXED".equalsIgnoreCase(config.getType())) {
            discount = config.getValue();
        }
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}