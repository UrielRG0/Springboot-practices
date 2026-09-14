package tacos.api.dto;

import lombok.Data;
import tacos.Ingredient.Type;

@Data
public class IngredientCreateRequest {
    private String id;
    private String name;
    private Type type;
    
}
