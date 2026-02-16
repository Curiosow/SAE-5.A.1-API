package fr.uphf.sae5a1api.controllers;

        import fr.uphf.sae5a1api.data.impl.actions.Rencontre;
        import fr.uphf.sae5a1api.data.sql.managers.actions.RencontreManager;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        /**
         * Contrôleur REST pour gérer les opérations liées aux rencontres.
         */
        @RestController
        public class RencontreController {

            /**
             * Récupère la liste de toutes les rencontres.
             *
             * <p>Ce endpoint expose un GET sur l'URL "/rencontre" et renvoie un objet JSON
             * contenant la liste des rencontres disponibles.</p>
             *
             * @return Une réponse HTTP contenant un objet JSON avec la liste des rencontres.
             *         Le format de la réponse est le suivant :
             *         <pre>
             *         {
             *             "docs": [ ... ]
             *         }
             *         </pre>
             */
            @GetMapping(value = "/rencontre", produces = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<Map<String, Object>> rencontre() {
                List<Rencontre> allRencontres = RencontreManager.getAllRencontres();

                Map<String, Object> responseJson = new HashMap<>();
                responseJson.put("docs", allRencontres);

                return ResponseEntity.ok(responseJson);
            }

        }