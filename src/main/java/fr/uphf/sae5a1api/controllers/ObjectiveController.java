package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Objective;
import fr.uphf.sae5a1api.data.sql.managers.actions.ObjectiveManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ObjectiveController {

    // GET : Récupérer
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/objective", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAll() {
        List<Objective> allObjectives = ObjectiveManager.getAllObjectives();
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allObjectives); // IMPORTANT: Tu envoies { "docs": [...] }
        return ResponseEntity.ok(responseJson);
    }

    // POST : Ajouter
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/objective", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody Objective objective) {
        try {
            ObjectiveManager.createObjective(objective);
            return ResponseEntity.ok("Objectif créé avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de la création");
        }
    }

    // DELETE : Supprimer
    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping(value = "/objective/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        try {
            ObjectiveManager.deleteObjective(id);
            return ResponseEntity.ok("Objectif supprimé");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur suppression");
        }
    }
}