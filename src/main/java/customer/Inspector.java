package customer;

import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The health inspector, over the network this time.
 *
 * Checks a truck against the menu board's promises, using only the public
 * window. Exit code 0 = certified, 1 = failed inspection.
 */
public class Inspector {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        TruckWindow window = new TruckWindow(TruckWindow.truckUrl());
        System.out.println("Inspecting truck at " + window.baseUrl() + "\n");
        window.waitUntilOpen();

        HttpResponse<String> menu = window.get("/menu");
        check("Menu board is posted (GET /menu returns 200)", menu.statusCode() == 200);

        HttpResponse<String> order = window.post("/orders",
                "{\"dish\":\"chili\",\"servings\":10,\"heat\":\"hot\"}");
        check("Takes an order (POST /orders returns 201)", order.statusCode() == 201);

        Integer served = intField(order.body(), "servings");
        check("Serves what was ordered (asked 10, got " + served + ")",
                served != null && served == 10);

        HttpResponse<String> bad = window.post("/orders",
                "{\"dish\":\"tacos\",\"servings\":10}");
        check("Refuses off-menu items (tacos returns 400)", bad.statusCode() == 400);

        HttpResponse<String> recipe = window.get("/recipe");
        check("Kitchen is guarded (GET /recipe returns 403)", recipe.statusCode() == 403);

        System.out.println();
        if (failures == 0) {
            System.out.println("CERTIFIED. Serve it with pride.");
        } else {
            System.out.println("FAILED INSPECTION: " + failures + " problem(s). It implemented the interface. It still broke the contract.");
        }
        System.exit(failures == 0 ? 0 : 1);
    }

    private static void check(String what, boolean ok) {
        System.out.println((ok ? "  PASS  " : "  FAIL  ") + what);
        if (!ok) failures++;
    }

    private static Integer intField(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? Integer.valueOf(m.group(1)) : null;
    }
}
