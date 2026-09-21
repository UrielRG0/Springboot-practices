package tacos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;

@Data
public class TacoSearchCriteria {
    private String name;
    private String ingredientId;
    private String diet;
    private String excludeAllergen;
    private Integer spice;

    @Min(value=0, message="The page cannot be negative")
    private int page=0;

    @Min(value=1, message="The minimum size is 1")
    @Max(value=50, message="The maximum size allowed is 50 to avoid overload")
    private int size=20;

    private String sort = "createdAt,desc"; 
}