package kitchen;

import java.util.List;

/** Cal's pantry. Cal knows where everything is. You do not. */
public class Pantry {

    private final List<Ingredient> shelves = List.of(
            new Ingredient("ground beef"),
            new Ingredient("onion"),
            new Ingredient("garlic"),
            new Ingredient("kidney beans"),
            new Ingredient("crushed tomatoes"),
            new Ingredient("chili powder"),
            new Ingredient("cumin"));

    /** Starts a pot: browns the meat, then adds the basics. */
    public Chili brown(String meat, int servings) {
        Chili pot = new Chili("Cal", servings);
        pot.add(new Ingredient(meat));
        shelves.stream()
               .filter(i -> !i.name().equals(meat))
               .forEach(pot::add);
        return pot;
    }
}
