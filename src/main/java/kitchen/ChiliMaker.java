package kitchen;

/**
 * The interface: the only thing the outside world gets to see.
 *
 * Anything that calls itself a ChiliMaker promises one thing: ask for chili,
 * get chili. It says nothing about HOW the chili gets made.
 *
 * Contract (the part the compiler cannot check):
 * the pot that comes back must hold exactly the number of servings requested.
 */
public interface ChiliMaker {

    Chili makeChili(int servings);
}
