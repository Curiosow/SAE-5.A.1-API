package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Position;
import fr.uphf.sae5a1api.data.sql.managers.actions.PositionManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour gérer les opérations liées aux positions.
 */
@RestController
public class PositionController {

    /**
     * Récupère la liste de toutes les positions.
     *
     * <p>Ce endpoint expose un GET sur l'URL "/positions" et renvoie un objet JSON
     * contenant la liste des positions disponibles.</p>
     *
     * @return Une réponse HTTP contenant un objet JSON avec la liste des positions.
     *         Le format de la réponse est le suivant :
     *         <pre>
     *         {
     *             "docs": [ ... ]
     *         }
     *         </pre>
     */
    @GetMapping(value = "/positions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> match() {
        List<Position> allPositions = PositionManager.getAllPositions();

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("docs", allPositions);

        return ResponseEntity.ok(responseJson);
    }

}