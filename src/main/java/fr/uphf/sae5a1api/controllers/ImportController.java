package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
import fr.uphf.sae5a1api.data.sql.managers.actions.MatchManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
public class ImportController {

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/import/match-events")
    public ResponseEntity<String> importMatchEvents(@RequestBody Map<String, Object> payload) {
        try {
            String rencontreId = (String) payload.get("rencontreId");
            String adversaire = (String) payload.get("adversaire");
            String lieu = (String) payload.get("lieu");
            String dateStr = (String) payload.get("dateMatch"); // Format YYYY-MM-DD attendu

            List<Map<String, Object>> eventsData = (List<Map<String, Object>>) payload.get("events");

            // 1. Vérifier ou Créer le Match
            Match match = MatchManager.findByRencontreId(rencontreId);
            int matchId;

            if (match == null) {
                // Conversion de la date si présente
                LocalDate dateMatch = LocalDate.now();
                try {
                    if (dateStr != null) dateMatch = LocalDate.parse(dateStr);
                } catch (Exception e) { /* Ignorer erreur date */ }

                // CRÉATION AVEC TOUTES LES INFOS
                matchId = MatchManager.createMatch(rencontreId, adversaire, lieu, dateMatch);
                System.out.println("Match créé : " + adversaire + " (" + lieu + ")");
            } else {
                matchId = match.getId();
                System.out.println("Match existant trouvé : ID " + matchId);
            }

            // 2. Sauvegarder les événements (Code inchangé)
            int count = 0;
            for (Map<String, Object> eventMap : eventsData) {
                Evenement ev = new Evenement();
                ev.setMatchId(matchId);
                ev.setNom((String) eventMap.get("nom"));
                ev.setPosition(toDouble(eventMap.get("position")));
                ev.setDuree(toDouble(eventMap.get("duree")));
                ev.setDefense((String) eventMap.get("defense"));
                ev.setResultat((String) eventMap.get("resultat"));
                ev.setDefenseplus((String) eventMap.get("defenseplus"));
                ev.setJoueuse((String) eventMap.get("joueuse"));
                ev.setSecteur((String) eventMap.get("secteur"));
                ev.setAttaqueplacees((String) eventMap.get("attaqueplacees"));
                ev.setEnclenchements06((String) eventMap.get("enclenchements06"));
                ev.setLieupb((String) eventMap.get("lieupb"));
                ev.setPassed((String) eventMap.get("passed"));
                ev.setRepli((String) eventMap.get("repli"));
                ev.setDefensemoins((String) eventMap.get("defensemoins"));
                ev.setEnclenchementstransier((String) eventMap.get("enclenchementstransier"));
                ev.setGrandespace((String) eventMap.get("grandespace"));
                ev.setJets7m((String) eventMap.get("jets7m"));
                ev.setEnclenchements6c5((String) eventMap.get("enclenchements6c5"));

                EvenementManager.save(ev);
                count++;
            }

            return ResponseEntity.ok("Succès : " + count + " événements importés.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur : " + e.getMessage());
        }
    }

    private Double toDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try { return Double.parseDouble(obj.toString().replace(',', '.')); }
        catch (Exception e) { return 0.0; }
    }
}