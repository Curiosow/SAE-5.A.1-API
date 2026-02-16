package fr.uphf.sae5a1api.controllers;

        import fr.uphf.sae5a1api.data.impl.actions.Evenement;
        import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        /**
         * Contrôleur REST pour gérer les opérations liées aux événements.
         */
        @RestController
        public class EvenementController {

            /**
             * Récupère la liste de tous les événements.
             *
             * @return Une réponse HTTP contenant un objet JSON avec la liste des événements.
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @GetMapping(value = "/evenement", produces = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<Map<String, Object>> evenement() {
                List<Evenement> allEvenements = EvenementManager.getAllEvenements();
                Map<String, Object> responseJson = new HashMap<>();
                responseJson.put("docs", allEvenements);
                return ResponseEntity.ok(responseJson);
            }

            /**
             * Importe une liste d'événements dans la base de données.
             *
             * @param evenements La liste des événements à importer.
             * @return Une réponse HTTP indiquant le succès ou l'échec de l'importation.
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @PostMapping(value = "/evenement/import", consumes = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<String> importEvenements(@RequestBody List<Evenement> evenements) {
                try {
                    for (Evenement ev : evenements) {
                        EvenementManager.save(ev);
                    }
                    return ResponseEntity.ok("Importation réussie : " + evenements.size() + " événements ajoutés.");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Erreur lors de l'importation : " + e.getMessage());
                }
            }
        }