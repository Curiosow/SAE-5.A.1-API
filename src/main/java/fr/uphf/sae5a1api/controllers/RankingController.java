package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.actions.RankedTeam;
import fr.uphf.sae5a1api.data.sql.managers.data.RankingManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RankingController {

    @GetMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> ranking() {
        List<RankedTeam> allTeams = RankingManager.getAllTeams();

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allTeams);

        return ResponseEntity.ok(responseJson);
    }

}
