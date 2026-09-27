package tacos.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReorderQuoteDTO {
    private String message;
    private BigDecimal oldTotal;
    private BigDecimal newTotal;
    private boolean requiresConfirmation;
}