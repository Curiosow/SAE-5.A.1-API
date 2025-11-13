package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Team;
import fr.uphf.sae5a1api.data.sql.managers.actions.TeamManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TeamController {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/teamlogo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> team() {
        // Récupération des données depuis la BDD
        List<Team> allteams = TeamManager.getAllTeams();

        // Construction de la réponse JSON
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allteams);

        return ResponseEntity.ok(responseJson);
    }

}