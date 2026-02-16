package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.managers.actions.MatchManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour gérer les opérations liées aux matchs.
 */
@RestController
public class MatchController {

    /**
     * Récupère la liste de tous les matchs.
     *
     * <p>Ce endpoint expose un GET sur l'URL "/match" et renvoie un objet JSON
     * contenant la liste des matchs disponibles.</p>
     *
     * @return Une réponse HTTP contenant un objet JSON avec la liste des matchs.
     * Le format de la réponse est le suivant :
     * <pre>
     *         {
     *             "docs": [ ... ]
     *         }
     *         </pre>
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/match", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> match() {
        List<Match> allmatch = MatchManager.getAllMatchs();

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allmatch);

        return ResponseEntity.ok(responseJson);
    }

}