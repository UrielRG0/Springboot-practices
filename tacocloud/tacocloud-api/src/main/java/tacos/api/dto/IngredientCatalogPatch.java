package tacos.api.dto;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class IngredientCatalogPatch {
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;
    private Boolean available;
}