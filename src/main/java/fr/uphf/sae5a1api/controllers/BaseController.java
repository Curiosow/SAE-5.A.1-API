package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Level;

@Controller
public class BaseController {

    @GetMapping(value = "/")
    public ResponseEntity<String> pong() {
        SAE5A1ApiApplication.getLogger().log(Level.FINE, "Starting services : OK.");
        return new ResponseEntity<>("Server response : " + HttpStatus.OK.name(), HttpStatus.OK);
    }

}
