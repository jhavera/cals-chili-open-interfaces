package kitchen;

import java.time.LocalDate;

/**
 * It implements ChiliMaker, so it compiles. It even returns Chili.
 * But look closely at what it does with the servings you asked for.
 * The compiler checks the shape of the promise. Only a test checks the promise.
 */
public class CanOfChili implements ChiliMaker {

    private final LocalDate expires = LocalDate.of(2031, 6, 1);
    private final boolean needsCanOpener = true;

    @Override
    public Chili makeChili(int servings) {
        Chili pot = new Chili("CanOfChili", 2);   // one can is one can
        pot.add(new Ingredient("whatever is in the can"));
        return pot;
    }
}
