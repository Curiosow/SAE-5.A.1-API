package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.teams.Team;
import fr.uphf.sae5a1api.data.sql.managers.teams.TeamsManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TeamsController {

    @GetMapping(value = "/teams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> rencontre() {
        List<Team> allRencontres = TeamsManager.getAllTeams();

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allRencontres);

        return ResponseEntity.ok(responseJson);
    }

}
