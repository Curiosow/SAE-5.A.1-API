package fr.uphf.sae5a1api.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.data.impl.users.Coach;
import fr.uphf.sae5a1api.data.impl.users.Player;
import fr.uphf.sae5a1api.data.impl.users.User;
import fr.uphf.sae5a1api.data.sql.managers.users.UserManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Contrôleur REST pour gérer les opérations d'authentification et de gestion des utilisateurs.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Constantes pour les noms des champs
    public final String FIRST_NAME = "first_name";
    public final String LAST_NAME = "last_name";
    public final String EMAIL = "email";
    public final String IS_ACTIVE = "is_active";
    public final String UPDATED_AT = "updated_at";
    public final String TEAM_ID = "team_id";

    /**
     * Enregistre un nouvel utilisateur de type Coach.
     *
     * @param email     L'adresse e-mail du coach.
     * @param password  Le mot de passe du coach.
     * @param firstName Le prénom du coach.
     * @param lastName  Le nom de famille du coach.
     * @param team_id   L'identifiant de l'équipe associée.
     * @return Une réponse HTTP indiquant le succès ou l'échec de l'opération.
     */
    @PostMapping("/register_coach")
    public ResponseEntity<String> registerCoach(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String team_id) {

        User user = new Coach(
                UUID.randomUUID(),
                email,
                BCrypt.withDefaults().hashToString(12, password.toCharArray()),
                firstName,
                lastName,
                true,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                UUID.fromString(team_id)
        );

        UserManager.createUser(user);
        return ResponseEntity.ok("Création de l'utilisateur réussie !");
    }

    /**
     * Enregistre un nouvel utilisateur de type Player.
     *
     * @param email         L'adresse e-mail du joueur.
     * @param password      Le mot de passe du joueur.
     * @param firstName     Le prénom du joueur.
     * @param lastName      Le nom de famille du joueur.
     * @param team_id       L'identifiant de l'équipe associée.
     * @param jersey_number Le numéro de maillot du joueur.
     * @param birth_date    La date de naissance du joueur (format : yyyy-MM-dd).
     * @param height_cm     La taille du joueur en centimètres.
     * @return Une réponse HTTP indiquant le succès ou l'échec de l'opération.
     */
    @PostMapping("/register_player")
    public ResponseEntity<String> registerPlayer(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String team_id,
            @RequestParam Integer jersey_number,
            @RequestParam String birth_date,
            @RequestParam Integer height_cm) {

        Date birthDateParsed;
        try {
            birthDateParsed = new SimpleDateFormat("yyyy-MM-dd").parse(birth_date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Format de date invalide (attendu : yyyy-MM-dd)");
        }

        String nom_csv = Normalizer.normalize(firstName, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .trim()
                .toUpperCase();

        Player player = new Player(
                UUID.randomUUID(),
                UUID.fromString(team_id),
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

        UserManager.createUser(player);
        return ResponseEntity.ok("Création de l'utilisateur réussie !");
    }

    /**
     * Supprime un joueur en fonction de son adresse e-mail.
     *
     * @param email L'adresse e-mail du joueur à supprimer.
     * @return Une réponse HTTP indiquant le succès de l'opération.
     */
    @PostMapping("delete_player")
    public ResponseEntity<String> deletePlayer(@RequestParam String email) {
        UserManager.deleteUser(email);
        return ResponseEntity.ok("Compte de " + email + " supprimé avec succès !");
    }

    /**
     * Authentifie un utilisateur en fonction de son e-mail et de son mot de passe.
     *
     * @param email    L'adresse e-mail de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @return Une réponse HTTP indiquant le succès ou l'échec de l'authentification.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        User user = UserManager.login(email, password);
        if (user == null)
            return ResponseEntity.status(401).body("L'adresse e-mail ou le mot de passe est incorrect !");
        return ResponseEntity.ok("Connexion réussie pour " + user.getFirst_name() + " " + user.getLast_name());
    }

    /**
     * Récupère la liste des membres.
     *
     * @return Une réponse HTTP contenant la liste des membres.
     */
    @GetMapping("/members")
    public ResponseEntity<List<Map<String, Object>>> getMembers() {
        List<Map<String, Object>> members = new ArrayList<>();
        UserManager.getMembers(rs -> {
            try {
                Map<String, Object> member = new HashMap<>();
                member.put(FIRST_NAME, rs.getString(FIRST_NAME));
                member.put(LAST_NAME, rs.getString(LAST_NAME));
                member.put(EMAIL, rs.getString(EMAIL));
                member.put("account_type", rs.getString("account_type"));
                member.put(TEAM_ID, ((UUID) rs.getObject(TEAM_ID)).toString());
                members.add(member);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.ok(members);
    }

    /**
     * Récupère les informations d'un utilisateur par son e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Une réponse HTTP contenant les informations de l'utilisateur.
     */
    @GetMapping("/userbymail")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@RequestParam String email) {
        Map<String, Object> member = new HashMap<>();

        UserManager.getMemberByMail(email, rs -> {
            try {
                member.put(FIRST_NAME, rs.getString(FIRST_NAME));
                member.put(LAST_NAME, rs.getString(LAST_NAME));
                member.put(EMAIL, rs.getString(EMAIL));
                member.put("account_type", rs.getString("account_type"));
                member.put(TEAM_ID, ((UUID) rs.getObject(TEAM_ID)).toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(member);
    }

    /**
     * Récupère la liste des joueurs.
     *
     * @return Une réponse HTTP contenant la liste des joueurs.
     */
    @GetMapping("/players")
    public ResponseEntity<List<Map<String, Object>>> getPlayers() {
        List<Map<String, Object>> players = new ArrayList<>();
        UserManager.getPlayer(rs -> {
            try {
                Map<String, Object> player = new HashMap<>();
                player.put("id", rs.getString("id"));
                player.put(TEAM_ID, ((UUID) rs.getObject(TEAM_ID)).toString());
                player.put(FIRST_NAME, rs.getString(FIRST_NAME));
                player.put(LAST_NAME, rs.getString(LAST_NAME));
                player.put("team_name", rs.getString("team_name"));
                player.put(EMAIL, rs.getString(EMAIL));
                player.put("jersey_number", rs.getInt("jersey_number"));
                player.put("birth_date", rs.getDate("birth_date"));
                player.put("height_cm", rs.getInt("height_cm"));
                player.put(IS_ACTIVE, rs.getBoolean(IS_ACTIVE));
                player.put(UPDATED_AT, rs.getTimestamp(UPDATED_AT));
                player.put("picture", rs.getString("picture"));
                player.put("nom_csv", rs.getString("nom_csv"));
                players.add(player);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.ok(players);
    }

    /**
     * Récupère la liste des coachs.
     *
     * @return Une réponse HTTP contenant la liste des coachs.
     */
    @GetMapping("/coaches")
    public ResponseEntity<List<Map<String, Object>>> getCoaches() {
        List<Map<String, Object>> coaches = new ArrayList<>();
        UserManager.getCoach(rs -> {
            try {
                Map<String, Object> coach = new HashMap<>();
                coach.put(EMAIL, rs.getString(EMAIL));
                coach.put(FIRST_NAME, rs.getString(FIRST_NAME));
                coach.put(LAST_NAME, rs.getString(LAST_NAME));
                coach.put(IS_ACTIVE, rs.getBoolean(IS_ACTIVE));
                coach.put(UPDATED_AT, rs.getTimestamp(UPDATED_AT));
                coach.put(TEAM_ID, ((UUID) rs.getObject(TEAM_ID)).toString());
                coaches.add(coach);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.ok(coaches);
    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param coach    Indique si l'utilisateur est un coach.
     * @param type     Le type d'information à mettre à jour.
     * @param newValue La nouvelle valeur de l'information.
     * @param email    L'adresse e-mail de l'utilisateur.
     * @return Une réponse HTTP indiquant le succès de l'opération.
     */
    @PostMapping("/update_information")
    public ResponseEntity<String> updateInformation(
            @RequestParam String coach,
            @RequestParam String type,
            @RequestParam(required = false) String newValue,
            @RequestParam String email) {
        boolean boolCoach = Boolean.parseBoolean(coach);

        Object parsedValue = convertNewValue(type, newValue);

        UserManager.updateInformation(boolCoach, type, parsedValue, email);
        return ResponseEntity.ok("Modification effectué !");
    }

    /**
     * Convertit une nouvelle valeur en fonction de son type.
     *
     * @param type     Le type de la valeur.
     * @param rawValue La valeur brute.
     * @return La valeur convertie.
     */
    private Object convertNewValue(String type, String rawValue) {
        if (rawValue == null || rawValue.isBlank() || rawValue.equalsIgnoreCase("null"))
            return null;

        return switch (type) {
            case IS_ACTIVE -> parseBoolean(rawValue);
            case "jersey_number", "height_cm", "weight_kg" -> parseInteger(type, rawValue);
            case TEAM_ID -> UUID.fromString(rawValue);
            case "birth_date" -> parseSqlDate(rawValue);
            case UPDATED_AT -> parseTimestamp(rawValue);
            case EMAIL -> rawValue.toLowerCase(Locale.ROOT).trim();
            case "password" -> BCrypt.withDefaults().hashToString(12, rawValue.toCharArray());
            default -> rawValue;
        };
    }

    /**
     * Convertit une chaîne en valeur booléenne.
     *
     * @param rawValue La chaîne à convertir.
     * @return La valeur booléenne correspondante.
     */
    private Boolean parseBoolean(String rawValue) {
        if (rawValue.equalsIgnoreCase("true") || rawValue.equals("1"))
            return true;
        if (rawValue.equalsIgnoreCase("false") || rawValue.equals("0"))
            return false;
        throw new IllegalArgumentException("Valeur booléenne invalide : " + rawValue);
    }

    /**
     * Convertit une chaîne en entier.
     *
     * @param field    Le champ concerné.
     * @param rawValue La chaîne à convertir.
     * @return L'entier correspondant.
     */
    private Integer parseInteger(String field, String rawValue) {
        try {
            return Integer.valueOf(rawValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valeur entière invalide pour " + field + " : " + rawValue, e);
        }
    }

    /**
     * Convertit une chaîne en date SQL.
     *
     * @param rawValue La chaîne à convertir.
     * @return La date SQL correspondante.
     */
    private java.sql.Date parseSqlDate(String rawValue) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false);
            return new java.sql.Date(formatter.parse(rawValue).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Format de date attendu 'yyyy-MM-dd' pour birth_date", e);
        }
    }

    /**
     * Convertit une chaîne en timestamp.
     *
     * @param rawValue La chaîne à convertir.
     * @return Le timestamp correspondant.
     */
    private Timestamp parseTimestamp(String rawValue) {
        if (rawValue.equalsIgnoreCase("now"))
            return new Timestamp(System.currentTimeMillis());
        try {
            return Timestamp.from(java.time.Instant.parse(rawValue));
        } catch (Exception ignored) {
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return new Timestamp(formatter.parse(rawValue).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Format de timestamp invalide pour updated_at (ISO-8601 recommandé)", e);
        }
    }

}
