package tacos.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacos.pricing.CouponService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/coupons", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        BigDecimal subtotal = new BigDecimal(request.get("subtotal").toString());

        BigDecimal discount = couponService.calculateDiscount(code, subtotal);

        boolean isValid = discount.compareTo(BigDecimal.ZERO) > 0;

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("discount", discount);

        return ResponseEntity.ok(response);
    }
}