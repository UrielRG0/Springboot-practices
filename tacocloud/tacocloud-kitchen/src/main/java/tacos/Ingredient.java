package tacos;

import lombok.Data;

@Data
public class Ingredient {

  private final String name;
  private final Type type;

  public Ingredient(String name, Type type) {
      this.name = name;
      this.type = type;
  }

  public enum Type {
    WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
  }

}
