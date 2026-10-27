package kitchen;

/**
 * Our object. State is private (the kitchen). Behavior is public (the window).
 */
public class Cal implements ChiliMaker {

    private SecretRecipe recipe = new SecretRecipe();
    private Pantry pantry = new Pantry();

    @Override
    public Chili makeChili(int servings) {
        Chili pot = pantry.brown("ground beef", servings);
        pot.add(secretIngredient());
        return pot.simmer(3);
    }

    private Ingredient secretIngredient() {
        return recipe.theThingCalWontTellYou();
    }
}
