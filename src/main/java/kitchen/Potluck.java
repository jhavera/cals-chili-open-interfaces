package kitchen;

import java.util.List;

/**
 * Polymorphism demo. The potluck only knows it was promised ChiliMakers.
 * It never asks who they are or how they cook.
 *
 * Run this class (green arrow next to main).
 */
public class Potluck {

    public static void main(String[] args) {
        List<ChiliMaker> cooks = List.of(
                new Cal(),
                new JohnsInstantPot(),
                new CanOfChili()
        );

        System.out.println("ACC FedLab potluck: 10 servings each, please\n");

        for (ChiliMaker cook : cooks) {
            Chili pot = cook.makeChili(10);
            System.out.println("  " + pot);
        }

        System.out.println("\nSame call. Three kitchens. Nobody saw a recipe.");
    }
}
