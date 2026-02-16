package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Level;

/**
 * Contrôleur de base pour gérer les opérations générales de l'application.
 */
@Controller
public class BaseController {

    /**
     * Endpoint pour vérifier si le serveur est opérationnel.
     *
     * <p>Ce endpoint expose un GET sur l'URL "/" et renvoie une réponse HTTP
     * indiquant que le serveur fonctionne correctement.</p>
     *
     * @return Une réponse HTTP contenant un message de confirmation et le statut HTTP 200 (OK).
     */
    @GetMapping(value = "/")
    public ResponseEntity<String> pong() {
        SAE5A1ApiApplication.getLogger().log(Level.FINE, "Starting services : OK.");

        return new ResponseEntity<>("Server response : " + HttpStatus.OK.name(), HttpStatus.OK);
    }

}