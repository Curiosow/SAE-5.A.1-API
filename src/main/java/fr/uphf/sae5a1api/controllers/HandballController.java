package fr.uphf.sae5a1api.controllers; // (Votre package)

// Imports pour le parsing CSV
import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Imports de votre architecture
import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.actions.ActionHandball;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5a1api.data.sql.managers.data.HandballDataManager;
import fr.uphf.sae5a1api.data.actions.Match;
import fr.uphf.sae5a1api.data.sql.managers.data.MatchManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


// Imports Spring
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Controller pour gérer l'import de CSV et l'affichage des stats.
 */
@RestController
@RequestMapping("/handball")
public class HandballController {

    /**
     * Crée un nouveau match et retourne son UUID.
     * URL: POST /handball/matches
     * Body: JSON attendu : { "teamId": "uuid", "adversaire": "Nom", "dateMatch": "YYYY-MM-DD", "lieu": "Domicile/Extérieur" }
     */
    @PostMapping("/matches")
    public ResponseEntity<?> createMatch(@RequestBody Map<String, String> payload) {
        try {
            UUID teamId = UUID.fromString(payload.get("teamId"));
            String adversaire = payload.get("adversaire");
            LocalDate dateMatch = LocalDate.parse(payload.get("dateMatch")); // Format YYYY-MM-DD
            String lieu = payload.get("lieu");

            if (adversaire == null || adversaire.isEmpty()) {
                return new ResponseEntity<>("Le nom de l'adversaire est requis", HttpStatus.BAD_REQUEST);
            }

            UUID newMatchId = UUID.randomUUID();
            Match newMatch = new Match(newMatchId, teamId, adversaire, dateMatch, lieu);

            MatchManager.createMatch(newMatch);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("matchId", newMatchId));

        } catch (IllegalArgumentException | DateTimeParseException e) {
            return new ResponseEntity<>("Données invalides (UUID ou date mal formatée): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur serveur lors de la création du match: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * NOUVEL ENDPOINT: Importe/Met à jour les joueurs d'une équipe via CSV.
     * URL: POST /handball/teams/{teamId}/upload-players
     */
    @PostMapping("/teams/{teamId}/upload-players")
    public ResponseEntity<String> uploadPlayersCsv(
            @PathVariable UUID teamId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Le fichier est vide", HttpStatus.BAD_REQUEST);
        }

        final int[] stats = new int[3]; // [0] = created, [1] = updated, [2] = errors

        try {
            DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
                connection.setAutoCommit(false); // Mode transaction

                final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setTrim(true)
                        .setIgnoreEmptyLines(true)
                        .build();

                try (Reader reader = new InputStreamReader(file.getInputStream());
                     CSVParser csvParser = csvFormat.parse(reader)) {

                    for (CSVRecord record : csvParser) {
                        if (!record.isConsistent()) {
                            System.err.println("Ligne joueur ignorée (inconsistante): " + record.getRecordNumber());
                            stats[2]++;
                            continue;
                        }

                        // --- ADAPTEZ CES NOMS DE COLONNES ---
                        String nomCsv = record.get("Nom"); // Ex: "Jean Dupont"
                        Integer jersey = getIntOrNull(record.get("NumeroMaillot"));
                        Integer weight = getIntOrNull(record.get("Poids (kg)"));
                        Integer height = getIntOrNull(record.get("Taille (cm)"));
                        // --- FIN ADAPTATION ---

                        if (nomCsv == null || nomCsv.isEmpty()) {
                            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "Ligne joueur ignorée (nom vide): " + record.getRecordNumber());
                            stats[2]++;
                            continue;
                        }

                        int jerseyVal = (jersey != null) ? jersey : 0;
                        int weightVal = (weight != null) ? weight : 0;
                        int heightVal = (height != null) ? height : 0;

                        try {
                            boolean created = HandballDataManager.upsertPlayerFromCsv(
                                    connection, teamId, nomCsv, jerseyVal, weightVal, heightVal
                            );
                            if (created) stats[0]++;
                            else stats[1]++;

                        } catch (SQLException e) {
                            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "Erreur BDD pour joueur '" + nomCsv + "': " + e.getMessage());
                            stats[2]++;
                        }
                    } // Fin de la boucle CSV

                    if (stats[2] > 0) {
                        SAE5A1ApiApplication.getLogger().log(Level.SEVERE, stats[2] + " erreurs rencontrées, annulation de la transaction.");
                        connection.rollback();
                    } else {
                        SAE5A1ApiApplication.getLogger().log(Level.FINE, "Importation joueurs réussie, commit de la transaction.");
                        connection.commit();
                    }

                } catch (Exception parseException) {
                    connection.rollback();
                    throw new RuntimeException("Erreur de parsing CSV: " + parseException.getMessage(), parseException);
                } finally {
                    connection.setAutoCommit(true);
                }
            }); // Fin executeVoidQuery

            if (stats[2] > 0) {
                return new ResponseEntity<>("Importation échouée. " + stats[2] + " erreurs. Transaction annulée.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String response = String.format("Importation joueurs terminée. Créés: %d, Mis à jour: %d",
                    stats[0], stats[1]);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return new ResponseEntity<>("Erreur critique lors de l'import des joueurs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Endpoint pour l'upload du fichier CSV d'actions de match.
     * URL: POST /handball/matches/{matchId}/upload
     */
    @PostMapping("/matches/{matchId}/upload")
    public ResponseEntity<String> uploadCsv(
            @PathVariable UUID matchId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Le fichier est vide", HttpStatus.BAD_REQUEST);
        }

        final UUID teamId;
        try {
            Match match = MatchManager.findMatchById(matchId);

            if (match == null) {
                return new ResponseEntity<>("Match non trouvé pour l'ID: " + matchId, HttpStatus.NOT_FOUND);
            }
            teamId = match.getTeamId();
            if (teamId == null) {
                return new ResponseEntity<>("Le match " + matchId + " n'a pas de teamId associé.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la vérification du match: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        List<ActionHandball> actionsToSave;

        try {
            // Lecture CSV
            actionsToSave = DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
                List<ActionHandball> parsedActions = new ArrayList<>();

                final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setTrim(true)
                        .setIgnoreEmptyLines(true)
                        .setAllowMissingColumnNames(true)
                        .build();

                try (Reader reader = new InputStreamReader(file.getInputStream());
                     CSVParser csvParser = csvFormat.parse(reader))
                {
                    for (CSVRecord record : csvParser) {
                        if (!record.isConsistent()) {
                            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "Attention: Ligne CSV inconsistante ignorée: " + record.getRecordNumber());
                            continue;
                        }

                        String nomJoueur = record.get("Joueuses");
                        UUID joueurId = HandballDataManager.findJoueurId(connection, nomJoueur, teamId);

                        String detailAction = getFirstNonEmpty(
                                record.isMapped("Déf +") ? record.get("Déf +") : null,
                                record.isMapped("Déf -") ? record.get("Déf -") : null,
                                record.isMapped("Passes D") ? record.get("Passes D") : null,
                                record.isMapped("Lieu PB") ? record.get("Lieu PB") : null
                        );

                        // --- CORRECTION ---
                        // "Attaques placées" contient du texte ("Att 0-6").
                        // On l'ajoute à 'detailEnclenchement'.
                        String detailEnclenchement = getFirstNonEmpty(
                                record.isMapped("Attaques placées") ? record.get("Attaques placées") : null, // AJOUTÉ
                                record.isMapped("Enclenchements 06") ? record.get("Enclenchements 06") : null,
                                record.isMapped("Enclenchements 6 c 5") ? record.get("Enclenchements 6 c 5") : null,
                                record.isMapped("Enclenchements transition ER") ? record.get("Enclenchements transition ER") : null
                        );
                        // --- FIN CORRECTION ---


                        ActionHandball action = new ActionHandball(
                                matchId, joueurId,
                                // "Durée:" -> timestampMs (Long)
                                getLongOrNull(record.get("Durée:")),

                                // --- CORRECTION ---
                                // On n'a pas de colonne pour dureeActionMs (Integer), on passe 'null'.
                                // L'ancienne valeur (Attaques placées) est maintenant dans detailEnclenchement.
                                null, // ANCIENNEMENT: getIntOrNull(record.get("Attaques placées")),
                                // --- FIN CORRECTION ---

                                record.get("Position"),
                                record.get("Résultats"),
                                record.get("Secteurs"),
                                record.get("Défenses"),
                                detailAction,
                                detailEnclenchement);
                        parsedActions.add(action);
                    }
                }
                return parsedActions;
            });

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException && e.getMessage().contains("Mapping not found")) {
                return new ResponseEntity<>("Erreur de Parsing CSV: Colonne attendue non trouvée. Vérifiez les en-têtes du fichier. Détail: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Erreur critique lors du parsing CSV ou de la recherche joueur: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (actionsToSave == null || actionsToSave.isEmpty()) {
            return new ResponseEntity<>("Aucune action à importer (fichier vide ou erreur de parsing ?)", HttpStatus.BAD_REQUEST);
        }

        // Import en batch
        try {
            HandballDataManager.importActionsEnBatch(actionsToSave);
            return new ResponseEntity<>("Importation réussie: " + actionsToSave.size() + " actions ajoutées.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de l'écriture en BDD: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Endpoints pour les Stats ---
    @GetMapping("/stats/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestParam UUID matchId,
            @RequestParam(required = false) UUID joueurId) {
        try {
            Map<String, Object> stats = HandballDataManager.getSummaryStats(matchId, joueurId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats/heatmap")
    public ResponseEntity<List<Map<String, Object>>> getHeatmap(
            @RequestParam UUID matchId,
            @RequestParam(required = false) UUID joueurId) {
        try {
            List<Map<String, Object>> heatmapData = HandballDataManager.getHeatmap(matchId, joueurId);
            return ResponseEntity.ok(heatmapData);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Fonctions utilitaires ---
    private String getFirstNonEmpty(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private Long getLongOrNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Long.parseLong(value.trim());
        }
        catch (NumberFormatException e) {
            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "Erreur de parsing Long: '" + value + "'");
            return null;
        }
    }

    private Integer getIntOrNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException e) {
            SAE5A1ApiApplication.getLogger().log(Level.SEVERE, "Erreur de parsing Integer: '" + value + "'");
            return null;
        }
    }

    @GetMapping("/players/{joueurId}/stats")
    public ResponseEntity<Map<String, Object>> getPlayerStats(
            @PathVariable UUID joueurId,
            @RequestParam(required = false) UUID matchId) {

        try {
            Map<String, Object> stats = HandballDataManager.getPlayerProfileStats(joueurId, matchId);

            if (stats == null || stats.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Aucune statistique trouvée pour ce joueur ou ce match."), HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

