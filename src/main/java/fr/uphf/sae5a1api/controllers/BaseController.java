package fr.uphf.sae5a1api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    @GetMapping(value = "/")
    public ResponseEntity<String> pong() {
        System.out.println("Starting services : OK.");
        return new ResponseEntity<>("Server response : " + HttpStatus.OK.name(), HttpStatus.OK);
    }

}
