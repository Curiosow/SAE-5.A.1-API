package fr.uphf.sae5a1api.runnable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import fr.uphf.sae5a1api.data.actions.RankedTeam;
import fr.uphf.sae5a1api.data.impl.actions.Rencontre;
import fr.uphf.sae5a1api.data.sql.managers.data.RankingManager;
import fr.uphf.sae5a1api.data.sql.managers.actions.RencontreManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Level;

public class GetRencontreTask implements Runnable {

    private final String URL = "https://api.ligue-feminine-handball.fr/meet?sort=calendrier_journee_numero&asc=calendrier_journee_numero&competitionId=168256&limit=1000";

    @Override
    public void run() {
        SAE5A1ApiApplication.getLogger().log(Level.INFO, "Asking to API the planning of the league...");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBody = response.body();

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();

                JsonNode rootNode = objectMapper.readTree(jsonBody);
                JsonNode docsNode = rootNode.get("docs");

                List<Rencontre> rencontres = objectMapper.convertValue(
                        docsNode,
                        new TypeReference<List<Rencontre>>() {}
                );
                System.out.println("Find " + rencontres.size() + " Rencontres.");
                //System.out.println(rencontres.get(0).toString());

                SAE5A1ApiApplication.getLogger().log(Level.FINE, "API response has been processed!");

                for (Rencontre rencontre : rencontres) {
                    RencontreManager.save(rencontre);
                }
            } else {
                SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "An error occurred while running the asking of API. The return code was not 200, it was " + response.statusCode() + ".");
            }

        } catch (IOException | InterruptedException e) {
            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "An error occurred while running the asking of API or processing JSON.", e); // Log de l'exception
            throw new RuntimeException(e);
        }
    }
}
