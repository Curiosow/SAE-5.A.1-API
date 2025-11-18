package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EvenementController {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/evenement", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> evenement() {
        // Récupération des données depuis la BDD
        List<Evenement> allEvenements = EvenementManager.getAllEvenements();

        // Construction de la réponse JSON
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allEvenements);

        return ResponseEntity.ok(responseJson);
    }

}