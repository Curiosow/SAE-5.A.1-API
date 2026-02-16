package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
import fr.uphf.sae5a1api.data.sql.managers.actions.MatchManager;
import fr.uphf.sae5a1api.dto.ImportMatchRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Contrôleur REST responsable de l'importation d'événements liés à un match.
 *
 * Ce contrôleur expose un endpoint POST qui reçoit un JSON décrivant une
 * rencontre, ses métadonnées (équipe, adversaire, lieu, date) et une liste
 * d'événements (instances de {@link Evenement}).
 *
 * Comportement principal de l'endpoint :
 * - Valide la présence d'événements dans la demande.
 * - Vérifie s'il existe déjà un {@link Match} lié à la rencontre (via
 *   {@code MatchManager.findByRencontreId}). Si non, crée un match.
 * - Pour chaque {@link Evenement} reçu, injecte l'ID du match et de l'équipe
 *   puis persiste l'événement via {@code EvenementManager.save}.
 *
 * Le endpoint renvoie une réponse 200 avec le nombre d'événements importés
 * en cas de succès, 400 pour une requête invalide (liste vide) ou 500 en cas
 * d'erreur serveur.
 */
@RestController
public class ImportController {

    /**
     * Endpoint d'import des événements d'un match.
     *
     * Exposé à POST /api/import/match-events et attend un corps JSON conforme
     * à la DTO {@link ImportMatchRequest} :
     * <ul>
     *   <li>{@code rencontreId} (int) : identifiant externe de la rencontre</li>
     *   <li>{@code teamId} (int) : identifiant de l'équipe propriétaire des événements</li>
     *   <li>{@code dateMatch} (String, "yyyy-MM-dd") : optionnel, date du match</li>
     *   <li>{@code adversaire} (String) : nom de l'adversaire (optionnel pour la création)</li>
     *   <li>{@code lieu} (String) : lieu du match (optionnel pour la création)</li>
     *   <li>{@code events} (List<Evenement>) : liste des événements à importer</li>
     * </ul>
     *
     * @param request le contenu de la requête d'import sérialisé en {@link ImportMatchRequest}
     * @return ResponseEntity<String> contenant un message de succès ou d'erreur
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/api/import/match-events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importMatchEvents(@RequestBody ImportMatchRequest request) {
        try {
            // Log d'entrée pour faciliter le debug lors des imports
            System.out.println("--- RÉCEPTION IMPORT ---");
            System.out.println("Rencontre ID : " + request.getRencontreId());
            System.out.println("Team ID      : " + request.getTeamId());

            // Validation simple : s'assurer que la liste d'événements n'est pas vide
            if (request.getEvents() == null || request.getEvents().isEmpty()) {
                return ResponseEntity.badRequest().body("Erreur : La liste d'événements est vide.");
            }

            // Test rapide du mapping JSON -> POJO : affiche le premier élément
            Evenement first = request.getEvents().get(0);
            System.out.println("TEST DATA -> Temps: " + first.getTempsFormat() + " | Phase: " + first.getPhaseJeu());

            // 1. GESTION DU MATCH
            // Rechercher un match existant lié à la rencontre. Si absent, création d'un nouveau match.
            int matchId;
            Match match = MatchManager.findByRencontreId(request.getRencontreId());

            if (match == null) {
                System.out.println("Match introuvable, création...");
                // Par défaut : date du jour si aucune date fournie ou format invalide
                LocalDate dateMatch = LocalDate.now();
                try {
                    if (request.getDateMatch() != null) dateMatch = LocalDate.parse(request.getDateMatch());
                } catch (Exception e) { /* Ignorer format date */ }

                matchId = MatchManager.createMatch(
                        request.getRencontreId(),
                        request.getAdversaire(),
                        request.getLieu(),
                        dateMatch
                );
            } else {
                matchId = match.getId();
            }

            // 2. SAUVEGARDE EN BASE
            // Pour chaque événement : associer l'ID du match et de l'équipe, puis persister.
            int count = 0;
            for (Evenement ev : request.getEvents()) {
                ev.setMatchId(matchId);
                ev.setTeamId(request.getTeamId()); // Injection de l'équipe
                EvenementManager.save(ev);
                count++;
            }

            System.out.println("--- IMPORT TERMINÉ (" + count + " lignes) ---");
            return ResponseEntity.ok("Succès : " + count + " événements importés.");

        } catch (Exception e) {
            // En cas d'erreur inattendue, log et renvoyer 500 avec message d'erreur
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }
}