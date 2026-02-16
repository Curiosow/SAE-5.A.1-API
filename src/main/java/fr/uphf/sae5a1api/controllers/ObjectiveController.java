package fr.uphf.sae5a1api.controllers;

        import fr.uphf.sae5a1api.data.impl.actions.Objective;
        import fr.uphf.sae5a1api.data.sql.managers.actions.ObjectiveManager;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        /**
         * Contrôleur REST pour gérer les opérations liées aux objectifs.
         */
        @RestController
        public class ObjectiveController {

            /**
             * Récupère la liste de tous les objectifs.
             *
             * @return Une réponse HTTP contenant un objet JSON avec la liste des objectifs.
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @GetMapping(value = "/objective", produces = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<Map<String, Object>> getAll() {
                List<Objective> allObjectives = ObjectiveManager.getAllObjectives();
                Map<String, Object> responseJson = new HashMap<>();
                responseJson.put("docs", allObjectives);
                return ResponseEntity.ok(responseJson);
            }

            /**
             * Crée un nouvel objectif.
             *
             * @param objective L'objectif à créer, fourni dans le corps de la requête.
             * @return Une réponse HTTP indiquant le succès ou l'échec de la création.
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @PostMapping(value = "/objective", consumes = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<String> create(@RequestBody Objective objective) {
                try {
                    ObjectiveManager.createObjective(objective);
                    return ResponseEntity.ok("Objectif créé avec succès");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Erreur lors de la création : " + e.getMessage());
                }
            }

            /**
             * Met à jour le statut d'un objectif existant.
             *
             * @param id      L'identifiant de l'objectif à mettre à jour.
             * @param payload Un objet JSON contenant le nouveau statut sous la clé "status".
             * @return Une réponse HTTP indiquant le succès ou l'échec de la mise à jour.
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @PatchMapping(value = "/objective/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<Void> updateStatus(@PathVariable int id, @RequestBody Map<String, String> payload) {
                String newStatus = payload.get("status");
                if (newStatus != null) {
                    try {
                        ObjectiveManager.updateStatus(id, newStatus);
                        return ResponseEntity.ok().build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().build();
                    }
                }
                return ResponseEntity.badRequest().build();
            }

            /**
             * Supprime un objectif existant.
             *
             * @param id L'identifiant de l'objectif à supprimer.
             * @return Une réponse HTTP indiquant le succès ou l'échec de la suppression.
             */
            @CrossOrigin(origins = "http://localhost:3000")
            @DeleteMapping(value = "/objective/{id}")
            public ResponseEntity<String> delete(@PathVariable int id) {
                try {
                    ObjectiveManager.deleteObjective(id);
                    return ResponseEntity.ok("Objectif supprimé");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Erreur suppression");
                }
            }
        }