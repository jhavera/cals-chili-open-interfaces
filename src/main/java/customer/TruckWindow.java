package customer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Everything a customer needs to talk to ANY truck that follows the menu board.
 * Notice there is nothing here about Cal, kitchens, or recipes. Only the URL.
 */
class TruckWindow {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3)).build();
    private final String baseUrl;

    TruckWindow(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    static String truckUrl() {
        String v = System.getenv("TRUCK_URL");
        return (v == null || v.isBlank()) ? "http://localhost:8080" : v;
    }

    String baseUrl() { return baseUrl; }

    HttpResponse<String> get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    /** Trucks take a few seconds to park. Wait for the window to open. */
    void waitUntilOpen() throws InterruptedException {
        for (int attempt = 1; attempt <= 30; attempt++) {
            try {
                if (get("/health").statusCode() == 200) return;
            } catch (Exception notYet) {
                // still parking
            }
            Thread.sleep(1000);
        }
        throw new IllegalStateException("No truck answered at " + baseUrl);
    }
}
