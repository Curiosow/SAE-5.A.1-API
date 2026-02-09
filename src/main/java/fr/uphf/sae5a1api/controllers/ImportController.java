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

@RestController
public class ImportController {

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/api/import/match-events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importMatchEvents(@RequestBody ImportMatchRequest request) {
        try {
            System.out.println("--- RÉCEPTION IMPORT ---");
            System.out.println("Rencontre ID : " + request.getRencontreId());
            System.out.println("Team ID      : " + request.getTeamId());

            if (request.getEvents() == null || request.getEvents().isEmpty()) {
                return ResponseEntity.badRequest().body("Erreur : La liste d'événements est vide.");
            }

            // Test du premier élément pour voir si le mapping JSON a fonctionné
            Evenement first = request.getEvents().get(0);
            System.out.println("TEST DATA -> Temps: " + first.getTempsFormat() + " | Phase: " + first.getPhaseJeu());

            // 1. GESTION DU MATCH
            int matchId;
            Match match = MatchManager.findByRencontreId(request.getRencontreId());

            if (match == null) {
                System.out.println("Match introuvable, création...");
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
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }
}