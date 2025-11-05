package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.teams.RankedTeam;
import fr.uphf.sae5a1api.data.sql.managers.teams.RankingManager;
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

    @GetMapping(value = "/rankingAvesnois", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> rankingAvesnois() {
        RankedTeam teamAvesnois = RankingManager.getTeamAvesnois();

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("victoires", teamAvesnois.getClassement_nbr_match_gagne());
        responseJson.put("nuls", teamAvesnois.getClassement_nbr_match_nul());
        responseJson.put("defaites", teamAvesnois.getClassement_nbr_match_perdu());
        responseJson.put("points", teamAvesnois.getClassement_point_total());
        responseJson.put("position", teamAvesnois.getClassement_place());

        return ResponseEntity.ok(responseJson);
    }

}
