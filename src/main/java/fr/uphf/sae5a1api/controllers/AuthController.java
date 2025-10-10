package fr.uphf.sae5a1api.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.data.sql.managers.users.UserManager;
import fr.uphf.sae5a1api.data.users.Coach;
import fr.uphf.sae5a1api.data.users.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email, @RequestParam String password, @RequestParam String first_name, @RequestParam String last_name) {
        User user = new Coach(
                UUID.randomUUID(),
                email,
                BCrypt.withDefaults().hashToString(12, password.toCharArray()),
                first_name,
                last_name,
                true,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis())
        );

        UserManager.createUser(user);
        return ResponseEntity.ok("Création de l'utilisateur réussie !");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        User user = UserManager.login(email, password);
        if(user == null)
            return ResponseEntity.status(401).body("L'adresse e-mail ou le mot de passe est incorrect !");
        return ResponseEntity.ok("Connexion réussie pour " + user.getFirst_name() + " " + user.getLast_name());
    }
}
