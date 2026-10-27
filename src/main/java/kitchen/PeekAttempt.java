package kitchen;

/**
 * Encapsulation demo.
 *
 * LIVE DEMO: uncomment the last line in main and watch IntelliJ (and the
 * compiler) refuse. Expected error:
 *     'recipe' has private access in 'kitchen.Cal'
 * Then put the comment back so the project builds again.
 */
public class PeekAttempt {

    public static void main(String[] args) {
        Cal cal = new Cal();
        Chili lunch = cal.makeChili(12);          // fine, lunch is served
        System.out.println(lunch);

        // SecretRecipe r = cal.recipe;            // worth a shot
    }
}
