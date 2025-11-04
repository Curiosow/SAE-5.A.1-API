package fr.uphf.sae5a1api.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5a1api.data.sql.managers.users.UserManager;
import fr.uphf.sae5a1api.data.users.Coach;
import fr.uphf.sae5a1api.data.users.Player;
import fr.uphf.sae5a1api.data.users.Team;
import fr.uphf.sae5a1api.data.users.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;

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

        String nom_csv = java.text.Normalizer.normalize(firstName, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // "Léa" -> "Lea"
                .trim()                                            // " Lea " -> "Lea"
                .toUpperCase();




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
                new Date(System.currentTimeMillis()),
                null,
                nom_csv
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
                SAE5A1ApiApplication.getLogger().log(Level.INFO, "Membre trouvé : " + rs.getString("email"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok(members);
    }

    @GetMapping("/players")
    public ResponseEntity<List<Map<String, Object>>> getPlayers() {
        List<Map<String, Object>> players = new ArrayList<>();
        UserManager.getPlayer(rs -> {
            try {
                Map<String, Object> player = new HashMap<>();
                player.put("id", rs.getString("id"));
                player.put("first_name", rs.getString("first_name"));
                player.put("last_name", rs.getString("last_name"));
                player.put("team_name", rs.getString("team_name"));
                player.put("email", rs.getString("email"));
                player.put("jersey_number", rs.getInt("jersey_number"));
                player.put("birth_date", rs.getDate("birth_date"));
                player.put("height_cm", rs.getInt("height_cm"));
                player.put("is_active", rs.getBoolean("is_active"));
                player.put("updated_at", rs.getTimestamp("updated_at"));
                player.put("picture", rs.getString("picture"));
                players.add(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok(players);
    }

@GetMapping("/coaches")
    public ResponseEntity<List<Map<String, Object>>> getCoaches() {
        List<Map<String, Object>> coaches = new ArrayList<>();
        UserManager.getCoach(rs -> {
            try {
                Map<String, Object> coach = new HashMap<>();
                coach.put("email", rs.getString("email"));
                coach.put("first_name", rs.getString("first_name"));
                coach.put("last_name", rs.getString("last_name"));
                coach.put("is_active", rs.getBoolean("is_active"));
                coach.put("updated_at", rs.getTimestamp("updated_at"));
                coaches.add(coach);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok(coaches);
    }


    @PostMapping("/update_player_firstname")
    public ResponseEntity<String> updatePlayerFirstName(
            @RequestParam String email,
            @RequestParam String newFirstName) {
        UserManager.updatePlayerFirstName(email, newFirstName);
        return ResponseEntity.ok("Prénom du joueur mis à jour !");
    }

    @PostMapping("/update_player_lastname")
    public ResponseEntity<String> updatePlayerLastName(
            @RequestParam String email,
            @RequestParam String newLastName) {
        UserManager.updatePlayerLastName(email, newLastName);
        return ResponseEntity.ok("Nom du joueur mis à jour !");
    }

    @PostMapping("/update_player_number")
    public ResponseEntity<String> updatePlayerNumber(
            @RequestParam String email,
            @RequestParam Integer jerseyNumber) {
        UserManager.updatePlayerNumber(email, jerseyNumber);
        return ResponseEntity.ok("Numéro du joueur mis à jour !");
    }

    @PostMapping("/update_player_height")
    public ResponseEntity<String> updatePlayerHeight(
            @RequestParam String email,
            @RequestParam Integer heightCm) {
        UserManager.updatePlayerHeight(email, heightCm);
        return ResponseEntity.ok("Taille du joueur mise à jour !");
    }

    @PostMapping("/update_player_active")
    public ResponseEntity<String> updatePlayerActive(
            @RequestParam String email,
            @RequestParam Boolean isActive) {
        UserManager.updatePlayerActive(email, isActive);
        return ResponseEntity.ok("Statut d'activité du joueur mis à jour !");
    }

    @PostMapping("/update_player_email")
    public ResponseEntity<String> updatePlayerEmail(
            @RequestParam String oldEmail,
            @RequestParam String newEmail) {
        UserManager.updatePlayerEmail(oldEmail, newEmail);
        return ResponseEntity.ok("Email du joueur mis à jour !");
    }

    @PostMapping("/update_player_birthdate")
    public ResponseEntity<String> updatePlayerBirthDate(
            @RequestParam String email,
            @RequestParam String birthDate) {
        try {
            java.util.Date birthDateParsed = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
            UserManager.updatePlayerBirthDate(email, birthDateParsed);
            return ResponseEntity.ok("Date de naissance du joueur mise à jour !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Format de date invalide (attendu : yyyy-MM-dd)");
        }
    }

    @PostMapping("/update_coach_firstname")
    public ResponseEntity<String> updateCoachFirstName(
            @RequestParam String email,
            @RequestParam String newFirstName) {
        UserManager.updateCoachFirstName(email, newFirstName);
        return ResponseEntity.ok("Prénom du coach mis à jour !");
    }

    @PostMapping("/update_coach_lastname")
    public ResponseEntity<String> updateCoachLastName(
            @RequestParam String email,
            @RequestParam String newLastName) {
        UserManager.updateCoachLastName(email, newLastName);
        return ResponseEntity.ok("Nom du coach mis à jour !");
    }

    @PostMapping("/update_coach_email")
    public ResponseEntity<String> updateCoachEmail(
            @RequestParam String oldEmail,
            @RequestParam String newEmail) {
        UserManager.updateCoachEmail(oldEmail, newEmail);
        return ResponseEntity.ok("Email du coach mis à jour !");
    }

    @PostMapping("/update_coach_active")
    public ResponseEntity<String> updateCoachActive(
            @RequestParam String email,
            @RequestParam Boolean isActive) {
        UserManager.updateCoachActive(email, isActive);
        return ResponseEntity.ok("Statut d'activité du coach mis à jour !");
    }






















}
