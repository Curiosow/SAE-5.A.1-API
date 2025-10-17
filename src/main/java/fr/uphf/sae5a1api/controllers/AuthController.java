package fr.uphf.sae5a1api.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.data.sql.managers.users.UserManager;
import fr.uphf.sae5a1api.data.users.Coach;
import fr.uphf.sae5a1api.data.users.Player;
import fr.uphf.sae5a1api.data.users.Team;
import fr.uphf.sae5a1api.data.users.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register_coach")
    public ResponseEntity<String> registerCoach(@RequestParam String email, @RequestParam String password, @RequestParam String firstName, @RequestParam String lastName) {
        User user = new Coach(
                UUID.randomUUID(),
                email,
                BCrypt.withDefaults().hashToString(12, password.toCharArray()),
                firstName,
                lastName,
                true,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis())
        );

        UserManager.createUser(user);
        return ResponseEntity.ok("Création de l'utilisateur réussie !");
    }
    @PostMapping("/register_player")
    public ResponseEntity<String> registerPlayer(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam Integer jersey_number,
        @RequestParam String birth_date,
        @RequestParam Integer height_cm) {

        Date birthDateParsed;
        try {
            birthDateParsed = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(birth_date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Format de date invalide (attendu : yyyy-MM-dd)");
        }

        Player player = new Player(
                UUID.randomUUID(),
                UUID.fromString("e2a4c7b1-9f3d-4e6a-8b2c-7d1e5f4c3a2b"),
                email,
                BCrypt.withDefaults().hashToString(12, password.toCharArray()),
                firstName,
                lastName,
                jersey_number,
                birthDateParsed,
                height_cm,
                70,
                true,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis())
        );

        UserManager.createPlayer(player);
        return ResponseEntity.ok("Création de l'utilisateur réussie !");
    }

    /*@PostMapping("/register_team")
    public ResponseEntity<String> registerTeam(@RequestParam String name, @RequestParam String description, @RequestParam String season, @RequestParam boolean active, @RequestParam boolean is_active) {
        Team team = new Team(
                UUID.randomUUID(),
                null,
                name,
                description,
                season,
                active,
                is_active,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis())

        );

        UserManager.createUser(team);
        return ResponseEntity.ok("Création de l'utilisateur réussie !");
    }*/







    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        User user = UserManager.login(email, password);
        if(user == null)
            return ResponseEntity.status(401).body("L'adresse e-mail ou le mot de passe est incorrect !");
        return ResponseEntity.ok("Connexion réussie pour " + user.getFirst_name() + " " + user.getLast_name());
    }

    @GetMapping("/members")
    public ResponseEntity<List<Map<String, Object>>> getMembers() {
        List<Map<String, Object>> members = new ArrayList<>();
        UserManager.getMembers(rs -> {
            try {
                Map<String, Object> member = new HashMap<>();
                member.put("first_name", rs.getString("first_name"));
                member.put("last_name", rs.getString("last_name"));
                member.put("email", rs.getString("email"));
                member.put("account_type", rs.getString("account_type"));
                members.add(member);
                System.out.println("Membre trouvé : " + rs.getString("email"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok(members);
    }








}
