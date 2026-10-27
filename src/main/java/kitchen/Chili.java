package kitchen;

import java.util.ArrayList;
import java.util.List;

/** A pot of chili. What comes out of the window. */
public class Chili {

    private final String cook;
    private final int servings;
    private final List<String> contents = new ArrayList<>();
    private int hoursSimmered;

    public Chili(String cook, int servings) {
        this.cook = cook;
        this.servings = servings;
    }

    public Chili add(Ingredient ingredient) {
        contents.add(ingredient.name());
        return this;
    }

    public Chili simmer(int hours) {
        this.hoursSimmered += hours;
        return this;
    }

    public String cook() { return cook; }
    public int servings() { return servings; }
    public int hoursSimmered() { return hoursSimmered; }

    /** Customers can taste it. They cannot read the recipe. */
    @Override
    public String toString() {
        return String.format("%-18s %3d servings, simmered %d hr, %d ingredient(s)",
                cook, servings, hoursSimmered, contents.size());
    }
}
