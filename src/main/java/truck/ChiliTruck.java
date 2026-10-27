package truck;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kitchen.CanOfChili;
import kitchen.Cal;
import kitchen.Chili;
import kitchen.ChiliMaker;
import kitchen.JohnsInstantPot;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Act II: the food truck.
 *
 * The same ChiliMaker from Act I, now behind an HTTP window. The OpenAPI
 * menu board (openapi.yaml) describes this window. Customers can order;
 * nobody gets the recipe.
 *
 * Uses only the JDK's built-in web server, so there are no dependencies to
 * download and the container builds with nothing but a JDK.
 *
 * Settings (environment variables):
 *   KITCHEN       cal | instantpot | can     which ChiliMaker cooks (default cal)
 *   TRUCK_NAME    name printed in responses and logs
 *   PORT          port to listen on (default 8080)
 *   OPENAPI_PATH  where the menu board file lives (default openapi.yaml)
 */
public class ChiliTruck {

    private static final AtomicInteger NEXT_ORDER = new AtomicInteger(1047);
    private static final Set<String> HEAT_LEVELS = Set.of("mild", "medium", "hot");

    private final ChiliMaker cook;
    private final String truckName;

    ChiliTruck(ChiliMaker cook, String truckName) {
        this.cook = cook;
        this.truckName = truckName;
    }

    public static void main(String[] args) throws IOException {
        String kitchen = env("KITCHEN", "cal");
        String truckName = env("TRUCK_NAME", "Cal's Chili Truck");
        int port = Integer.parseInt(env("PORT", "8080"));

        ChiliMaker cook = switch (kitchen.toLowerCase()) {
            case "cal" -> new Cal();
            case "instantpot" -> new JohnsInstantPot();
            case "can" -> new CanOfChili();
            default -> throw new IllegalArgumentException("Unknown KITCHEN: " + kitchen);
        };

        ChiliTruck truck = new ChiliTruck(cook, truckName);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", truck::handle);
        server.start();
        truck.log("Window open on port " + port + " (kitchen: " + kitchen + ")");
    }

    // ------------------------------------------------------------------ the window

    private void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();
        addCorsHeaders(ex);   // lets the Swagger UI menu board call us from the browser

        try {
            if (method.equals("OPTIONS")) {
                send(ex, 204, null, null);
                return;
            }
            switch (path) {
                case "/health" -> onlyGet(ex, method, () -> sendJson(ex, 200, "{\"status\":\"open\"}"));
                case "/menu" -> onlyGet(ex, method, () -> sendJson(ex, 200, menuJson()));
                case "/orders" -> {
                    if (!method.equals("POST")) { sendJson(ex, 405, message("Orders are POST only.")); return; }
                    placeOrder(ex);
                }
                case "/recipe" -> onlyGet(ex, method, () -> {
                    log("GET /recipe  -> 403 (nice try)");
                    sendJson(ex, 403, message("Nice try."));
                });
                case "/openapi.yaml" -> onlyGet(ex, method, () -> serveMenuBoard(ex));
                default -> sendJson(ex, 404, message("Not on the menu."));
            }
        } catch (Exception e) {
            log("500 " + e);
            sendJson(ex, 500, message("Kitchen fire. Try again."));
        }
    }

    private void placeOrder(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String dish = stringField(body, "dish");
        Integer servings = intField(body, "servings");
        String heat = stringField(body, "heat");
        if (heat == null) heat = "medium";

        if (!"chili".equals(dish)) {
            log("POST /orders -> 400 (asked for " + dish + ")");
            sendJson(ex, 400, message("We only make chili."));
            return;
        }
        if (servings == null || servings < 1 || servings > 500) {
            log("POST /orders -> 400 (servings " + servings + ")");
            sendJson(ex, 400, message("Servings must be between 1 and 500."));
            return;
        }
        if (!HEAT_LEVELS.contains(heat)) {
            log("POST /orders -> 400 (heat " + heat + ")");
            sendJson(ex, 400, message("Heat must be mild, medium, or hot."));
            return;
        }

        // The Act I interface call. The truck has no idea how the kitchen works.
        Chili pot = cook.makeChili(servings);

        String orderId = "A-" + NEXT_ORDER.getAndIncrement();
        String json = "{"
                + "\"orderId\":\"" + orderId + "\","
                + "\"truck\":\"" + esc(truckName) + "\","
                + "\"dish\":\"chili\","
                + "\"heat\":\"" + heat + "\","
                + "\"servings\":" + pot.servings() + ","
                + "\"hoursSimmered\":" + pot.hoursSimmered() + ","
                + "\"eta\":\"20 minutes\","
                + "\"cornbread\":\"extra\""
                + "}";

        log("POST /orders " + servings + " servings, " + heat + " -> 201 " + orderId);
        sendJson(ex, 201, json);
    }

    private String menuJson() {
        return "{"
                + "\"truck\":\"" + esc(truckName) + "\","
                + "\"items\":[{\"dish\":\"chili\",\"heat\":[\"mild\",\"medium\",\"hot\"],\"maxServings\":500}],"
                + "\"note\":\"Recipe not included.\""
                + "}";
    }

    private void serveMenuBoard(HttpExchange ex) throws IOException {
        Path spec = Path.of(env("OPENAPI_PATH", "openapi.yaml"));
        if (!Files.exists(spec)) {
            sendJson(ex, 404, message("Menu board not mounted."));
            return;
        }
        send(ex, 200, "application/yaml", Files.readAllBytes(spec));
    }

    // ------------------------------------------------------------------ plumbing

    private interface Action { void run() throws IOException; }

    private static void onlyGet(HttpExchange ex, String method, Action action) throws IOException {
        if (method.equals("GET")) action.run();
        else sendJson(ex, 405, message("GET only."));
    }

    private static void addCorsHeaders(HttpExchange ex) {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
        send(ex, status, "application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private static void send(HttpExchange ex, int status, String type, byte[] body) throws IOException {
        if (type != null) ex.getResponseHeaders().set("Content-Type", type);
        if (body == null) {
            ex.sendResponseHeaders(status, -1);
        } else {
            ex.sendResponseHeaders(status, body.length);
            try (OutputStream out = ex.getResponseBody()) { out.write(body); }
        }
        ex.close();
    }

    private static String message(String text) {
        return "{\"message\":\"" + esc(text) + "\"}";
    }

    // Tiny JSON field readers. Fine for a demo; use a real JSON library in production.
    private static String stringField(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static Integer intField(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? Integer.valueOf(m.group(1)) : null;
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v;
    }

    private void log(String line) {
        System.out.println("[" + truckName + "] " + line);
    }
}
