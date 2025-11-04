package fr.uphf.sae5a1api.runnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uphf.sae5a1api.SAE5A1ApiApplication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class GetRankingTask implements Runnable {

    private final String URL = "https://api.ligue-feminine-handball.fr/ranking?poule_competition_id=168256&limit=1000&sort=classement_place&asc=classement_place";

    @Override
    public void run() {
        SAE5A1ApiApplication.getLogger().log(Level.INFO, "Asking to API the ranking of the league...");

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBody = response.body();

                SAE5A1ApiApplication.getLogger().log(Level.FINE, "API response has been applied in database!");
            } else {
                SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "An error occurred while running the asking of API. The return code was not 200, it was " + response.statusCode() + ".");
            }

        } catch (IOException | InterruptedException e) {
            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "An error occurred while running the asking of API.");
            throw new RuntimeException(e);
        }
    }

}
