package kitchen;

/**
 * Anti-pattern demo: "just add a getter for convenience."
 * This compiles. It also hands the secret to anyone who asks,
 * and now Cal can never change his recipe without breaking every caller.
 * Expose what Cal DOES, never what Cal HAS.
 */
public class LeakyCal implements ChiliMaker {

    private SecretRecipe recipe = new SecretRecipe();
    private Pantry pantry = new Pantry();

    @Override
    public Chili makeChili(int servings) {
        Chili pot = pantry.brown("ground beef", servings);
        pot.add(recipe.theThingCalWontTellYou());
        return pot.simmer(3);
    }

    // added "just for convenience"
    public SecretRecipe getRecipe() {
        return recipe;   // and there goes the secret
    }

    public static void main(String[] args) {
        LeakyCal cal = new LeakyCal();
        Ingredient secret = cal.getRecipe().theThingCalWontTellYou();
        System.out.println("Someone just walked off with: " + secret.name());
    }
}
