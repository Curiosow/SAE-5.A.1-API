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

/**
 * Contrôleur REST pour gérer les opérations liées aux équipes.
 */
@RestController
public class TeamsController {

    /**
     * Récupère la liste de toutes les équipes.
     *
     * <p>Ce endpoint expose un GET sur l'URL "/teams" et renvoie un objet JSON
     * contenant la liste des équipes disponibles.</p>
     *
     * @return Une réponse HTTP contenant un objet JSON avec la liste des équipes.
     *         Le format de la réponse est le suivant :
     *         <pre>
     *         {
     *             "docs": [ ... ]
     *         }
     *         </pre>
     */
    @GetMapping(value = "/teams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> rencontre() {
        List<Team> allRencontres = TeamsManager.getAllTeams();

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allRencontres);

        return ResponseEntity.ok(responseJson);
    }

}