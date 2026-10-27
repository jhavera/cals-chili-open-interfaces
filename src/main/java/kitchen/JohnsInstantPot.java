package kitchen;

/** Same interface, very different kitchen. Two buttons. Forty minutes. */
public class JohnsInstantPot implements ChiliMaker {

    private final int buttonsUnderstood = 2;
    private int pressure;

    @Override
    public Chili makeChili(int servings) {
        pressure = 11;   // psi, give or take
        Chili pot = new Chili("John's InstantPot", servings);
        pot.add(new Ingredient("ground turkey"))
           .add(new Ingredient("chili seasoning packet"))
           .add(new Ingredient("canned beans"));
        return pot.simmer(1);
    }
}
