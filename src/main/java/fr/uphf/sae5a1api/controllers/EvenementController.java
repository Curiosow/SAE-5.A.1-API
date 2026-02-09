package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EvenementController {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/evenement", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> evenement() {
        List<Evenement> allEvenements = EvenementManager.getAllEvenements();
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allEvenements);
        return ResponseEntity.ok(responseJson);
    }

    // --- NOUVELLE ROUTE POUR IMPORTER LES DONNÉES ---
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/evenement/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importEvenements(@RequestBody List<Evenement> evenements) {
        try {
            for (Evenement ev : evenements) {
                EvenementManager.save(ev);
            }
            return ResponseEntity.ok("Importation réussie : " + evenements.size() + " événements ajoutés.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de l'importation : " + e.getMessage());
        }
    }
}