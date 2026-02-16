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

        /**
         * Contrôleur REST pour gérer les opérations liées aux équipes.
         */
        @RestController
        public class TeamController {

            /**
             * Récupère la liste de toutes les équipes avec leurs logos.
             *
             * <p>Ce endpoint expose un GET sur l'URL "/teamlogo" et renvoie un objet JSON
             * contenant la liste des équipes disponibles avec leurs informations associées.</p>
             *
             * @return Une réponse HTTP contenant un objet JSON avec la liste des équipes.
             *         Le format de la réponse est le suivant :
             *         <pre>
             *         {
             *             "docs": [ ... ]
             *         }
             *         </pre>
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @GetMapping(value = "/teamlogo", produces = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<Map<String, Object>> team() {
                List<Team> allteams = TeamManager.getAllTeams();

                Map<String, Object> responseJson = new HashMap<>();
                responseJson.put("docs", allteams);

                return ResponseEntity.ok(responseJson);
            }

        }