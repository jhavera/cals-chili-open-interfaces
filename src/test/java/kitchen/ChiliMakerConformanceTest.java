package kitchen;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The health inspector.
 *
 * Every ChiliMaker compiled, so every one "conforms" to the interface's shape.
 * This test checks the interface's CONTRACT: ask for N servings, get N servings.
 *
 * Expected result: Cal and the InstantPot pass. CanOfChili fails.
 * That failure is the point of the demo.
 */
class ChiliMakerConformanceTest {

    static Stream<Arguments> cooks() {
        return Stream.of(
                Arguments.of("Cal", new Cal()),
                Arguments.of("JohnsInstantPot", new JohnsInstantPot()),
                Arguments.of("CanOfChili", new CanOfChili()));
    }

    @ParameterizedTest(name = "{0} serves what the menu says")
    @MethodSource("cooks")
    void servesTheRequestedNumberOfServings(String name, ChiliMaker cook) {
        Chili pot = cook.makeChili(10);

        assertNotNull(pot, name + " returned no chili at all");
        assertEquals(10, pot.servings(), name + " was asked for 10 servings");
    }
}
