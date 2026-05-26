import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static String jwtToken = null;

    public static void setToken(String token) { jwtToken = token; }

    public static boolean isAuthenticated() { return jwtToken != null && !jwtToken.isEmpty(); }

    public static void logout() { jwtToken = null; }

    public static CompletableFuture<HttpResponse<String>> postPublic(String endpoint, String jsonPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> getSecured(String endpoint) {
        if (!isAuthenticated())
            return CompletableFuture.failedFuture(new IllegalStateException("User is not authenticated."));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + jwtToken)
                .header("Accept", "application/json")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> postSecured(String endpoint, String jsonPayload) {
        if (!isAuthenticated())
            return CompletableFuture.failedFuture(new IllegalStateException("User is not authenticated."));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + jwtToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
