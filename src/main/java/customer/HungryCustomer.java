package customer;

import java.net.http.HttpResponse;

/**
 * Point-to-point call: the customer walks straight up to one truck's window.
 *
 * Which truck? Only the TRUCK_URL environment variable decides.
 * Swap the truck by changing that one setting. No code changes.
 */
public class HungryCustomer {

    public static void main(String[] args) throws Exception {
        TruckWindow window = new TruckWindow(TruckWindow.truckUrl());
        System.out.println("Hungry customer walking up to " + window.baseUrl());
        window.waitUntilOpen();

        HttpResponse<String> menu = window.get("/menu");
        System.out.println("\nGET /menu          -> " + menu.statusCode());
        System.out.println("  " + menu.body());

        HttpResponse<String> order = window.post("/orders",
                "{\"dish\":\"chili\",\"servings\":12,\"heat\":\"medium\"}");
        System.out.println("\nPOST /orders       -> " + order.statusCode());
        System.out.println("  " + order.body());

        HttpResponse<String> recipe = window.get("/recipe");
        System.out.println("\nGET /recipe        -> " + recipe.statusCode());
        System.out.println("  " + recipe.body());

        System.out.println("\nLunch is served. Recipe still secret.");
    }
}
