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

/**
 * Contrôleur REST pour gérer les opérations liées au classement des équipes.
 */
@RestController
public class RankingController {

    /**
     * Récupère le classement de toutes les équipes.
     *
     * <p>Ce endpoint expose un GET sur l'URL "/ranking" et renvoie un objet JSON
     * contenant la liste des équipes classées.</p>
     *
     * @return Une réponse HTTP contenant un objet JSON avec la liste des équipes classées.
     *         Le format de la réponse est le suivant :
     *         <pre>
     *         {
     *             "docs": [ ... ]
     *         }
     *         </pre>
     */
    @GetMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> ranking() {
        // Récupère toutes les équipes classées depuis le gestionnaire
        List<RankedTeam> allTeams = RankingManager.getAllTeams();

        // Prépare la réponse JSON
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allTeams);

        // Renvoie la réponse HTTP avec le contenu JSON
        return ResponseEntity.ok(responseJson);
    }

    /**
     * Récupère les statistiques de l'équipe des Avesnois.
     *
     * <p>Ce endpoint expose un GET sur l'URL "/rankingAvesnois" et renvoie un objet JSON
     * contenant les statistiques de l'équipe des Avesnois, telles que les victoires,
     * les nuls, les défaites, les points et la position.</p>
     *
     * @return Une réponse HTTP contenant un objet JSON avec les statistiques de l'équipe des Avesnois.
     *         Le format de la réponse est le suivant :
     *         <pre>
     *         {
     *             "victoires": int,
     *             "nuls": int,
     *             "defaites": int,
     *             "points": int,
     *             "position": int
     *         }
     *         </pre>
     */
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