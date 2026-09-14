package tacos.api.dto;

import tacos.Ingredient;

public class IngredientMapper {
    
    public static Ingredient toDomain(IngredientCreateRequest request){
        return new Ingredient(request.getId(), request.getName(), request.getType());
    }

    public static IngredientResponse toResponse(Ingredient ingredient){
        IngredientResponse response = new IngredientResponse();
        response.setId(ingredient.getId());
        response.setName(ingredient.getName());
        response.setType(ingredient.getType());
        return response;
    }

}
