package kitchen;

/**
 * The secret. Only Cal holds a reference to one of these.
 * (If you are reading this file during the talk, you are the reason
 * encapsulation exists.)
 */
public class SecretRecipe {

    public Ingredient theThingCalWontTellYou() {
        return new Ingredient("[REDACTED BY CAL]");
    }
}
