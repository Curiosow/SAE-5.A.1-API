package fr.uphf.sae5a1api.data.sql.managers.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.users.Coach;
import fr.uphf.sae5a1api.data.impl.users.Player;
import fr.uphf.sae5a1api.data.impl.users.User;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Classe de gestion des utilisateurs (coachs et joueurs).
 * Fournit des méthodes pour créer, supprimer, récupérer et mettre à jour les informations des utilisateurs.
 */
public class UserManager {

    // TABLES

    public static final String COACH_TABLE = "coaches";
    public static final String PLAYER_TABLE = "players";

    // REQUESTS

    // Savers
    public static final String SAVE_COACH = "INSERT INTO " + COACH_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SAVE_PLAYER = "INSERT INTO " + PLAYER_TABLE + " (id, email, password, first_name, last_name, is_active, created_at, updated_at, team_id, jersey_number, birth_date, height_cm, weight_kg, picture, nom_csv) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String DELETE_PLAYER = "DELETE FROM " + PLAYER_TABLE + " WHERE email = ?";

    // Getters
    public static final String GET_MEMBER_BY_MAIL = "SELECT first_name, last_name, email, team_id, 'player' AS account_type FROM " + PLAYER_TABLE + " WHERE email = ? UNION ALL SELECT first_name, last_name, email, team_id, 'coach' AS account_type FROM " + COACH_TABLE + " WHERE email = ?";
    public static final String GET_COACH_BY_MAIL = "SELECT * FROM " + COACH_TABLE + " where email = ?";
    public static final String GET_PLAYER_BY_MAIL = "SELECT * FROM " + PLAYER_TABLE + " where email = ?";

    // Global getters
    public static final String GET_MEMBERS = "SELECT first_name, last_name, email, team_id, 'player' AS account_type FROM " + PLAYER_TABLE + " UNION ALL SELECT first_name, last_name, email, team_id, 'coach' AS account_type FROM " + COACH_TABLE;
    public static final String GET_PLAYERS = "SELECT p.*, t.name AS team_name FROM " + PLAYER_TABLE + " p JOIN teams t ON p.team_id = t.id ";
    public static final String GET_COACHES = "SELECT * FROM " + COACH_TABLE;
    private static final Set<String> COACH_UPDATABLE_COLUMNS = Set.of(
            "first_name",
            "last_name",
            "email",
            "password",
            "team_id",
            "is_active",
            "updated_at"
    );
    private static final Set<String> PLAYER_UPDATABLE_COLUMNS = Set.of(
            "first_name",
            "last_name",
            "email",
            "password",
            "team_id",
            "is_active",
            "jersey_number",
            "birth_date",
            "height_cm",
            "weight_kg",
            "picture",
            "nom_csv",
            "updated_at"
    );

    // ---- IMPLEMENTATION ---- \\

    /**
     * Crée un utilisateur (coach ou joueur) dans la base de données.
     *
     * @param user L'utilisateur à créer.
     */
    public static void createUser(User user) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(user instanceof Coach ? SAVE_COACH : SAVE_PLAYER);

            statement.setObject(1, user.getUuid());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getFirst_name());
            statement.setString(5, user.getLast_name());
            statement.setBoolean(6, user.isActive());
            statement.setTimestamp(7, new Timestamp(user.getCreated_at().getTime()));
            statement.setTimestamp(8, new Timestamp(user.getUpdated_at().getTime()));
            statement.setObject(9, user.getTeam_id());

            if (user instanceof Player player) {
                statement.setObject(9, player.getTeam_id());
                statement.setInt(10, player.getJersey_number());
                statement.setDate(11, new java.sql.Date(player.getBirth_date().getTime()));
                statement.setInt(12, player.getHeight_cm());
                statement.setInt(13, player.getWeight_kg());
                statement.setString(14, player.getPicture_url());
                statement.setString(15, player.getNom_csv());
            }

            System.out.println(statement);
            statement.executeUpdate();
        });
    }

    /**
     * Supprime un utilisateur (joueur) par email.
     *
     * @param email L'email de l'utilisateur à supprimer.
     */
    public static void deleteUser(String email) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(DELETE_PLAYER);
            statement.setString(1, email);
            statement.executeUpdate();
        });
    }

    /**
     * Récupère tous les membres (coachs et joueurs) et applique une action sur chaque résultat.
     *
     * @param consumer Action à appliquer sur chaque ligne de résultat.
     */
    public static void getMembers(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_MEMBERS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    /**
     * Récupère un membre par email et applique une action sur le résultat.
     *
     * @param email    L'email du membre à récupérer.
     * @param consumer Action à appliquer sur le résultat.
     */
    public static void getMemberByMail(String email, Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_MEMBER_BY_MAIL);
            statement.setString(1, email);
            statement.setString(2, email);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    /**
     * Récupère tous les joueurs et applique une action sur chaque résultat.
     *
     * @param consumer Action à appliquer sur chaque ligne de résultat.
     */
    public static void getPlayer(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_PLAYERS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    /**
     * Récupère tous les coachs et applique une action sur chaque résultat.
     *
     * @param consumer Action à appliquer sur chaque ligne de résultat.
     */
    public static void getCoach(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACHES);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    /**
     * Met à jour une information spécifique pour un utilisateur (coach ou joueur).
     *
     * @param coach    Indique si l'utilisateur est un coach.
     * @param type     Colonne à mettre à jour.
     * @param newValue Nouvelle valeur.
     * @param email    Email de l'utilisateur à mettre à jour.
     */
    public static void updateInformation(boolean coach, String type, Object newValue, String email) {
        final String tableName = coach ? COACH_TABLE : PLAYER_TABLE;
        final Set<String> allowedColumns = coach ? COACH_UPDATABLE_COLUMNS : PLAYER_UPDATABLE_COLUMNS;

        if (!allowedColumns.contains(type)) {
            throw new IllegalArgumentException("Colonne non autorisée pour la mise à jour : " + type);
        }

        final String updateQuery = "UPDATE " + tableName + " SET " + type + " = ? WHERE email = ?";

        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(updateQuery);

            if (newValue == null) {
                statement.setNull(1, Types.NULL);
            } else if (newValue instanceof String) {
                statement.setString(1, (String) newValue);
            } else if (newValue instanceof Integer) {
                statement.setInt(1, (Integer) newValue);
            } else if (newValue instanceof Boolean) {
                statement.setBoolean(1, (Boolean) newValue);
            } else if (newValue instanceof java.sql.Date date) {
                statement.setDate(1, date);
            } else if (newValue instanceof Timestamp timestamp) {
                statement.setTimestamp(1, timestamp);
            } else if (newValue instanceof UUID uuid) {
                statement.setObject(1, uuid);
            } else if (newValue instanceof Date legacyDate) {
                statement.setTimestamp(1, new Timestamp(legacyDate.getTime()));
            } else {
                throw new IllegalArgumentException("Type de valeur non pris en charge : " + newValue.getClass());
            }

            statement.setString(2, email);

            statement.executeUpdate();
        });
    }

    /**
     * Authentifie un utilisateur en vérifiant son email et son mot de passe.
     *
     * @param email       L'email de l'utilisateur.
     * @param rawPassword Le mot de passe non chiffré.
     * @return L'utilisateur authentifié ou null si les informations sont incorrectes.
     */
    public static User login(String email, String rawPassword) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACH_BY_MAIL);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                if (BCrypt.verifyer().verify(rawPassword.toCharArray(), resultSet.getString("password")).verified)
                    return buildCoach(resultSet);
                else
                    return null;
            } else {
                statement = connection.prepareStatement(GET_PLAYER_BY_MAIL);
                statement.setString(1, email);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    System.out.println(resultSet.getString("first_name"));
                    if (BCrypt.verifyer().verify(rawPassword.toCharArray(), resultSet.getString("password")).verified)
                        return buildPlayer(resultSet);
                    else
                        return null;
                } else
                    return null;
            }

        });
    }

    /**
     * Construit un objet Coach à partir d'un ResultSet.
     *
     * @param rs Le ResultSet contenant les données.
     * @return Un objet Coach.
     * @throws SQLException En cas d'erreur SQL.
     */
    private static Coach buildCoach(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject("id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        boolean isActive = rs.getBoolean("is_active");
        Date createdAt = rs.getTimestamp("created_at");
        Date updatedAt = rs.getTimestamp("updated_at");
        UUID teamId = (UUID) rs.getObject("team_id");

        return new Coach(uuid, email, password, firstName, lastName, isActive, createdAt, updatedAt, teamId);
    }

    /**
     * Construit un objet Player à partir d'un ResultSet.
     *
     * @param rs Le ResultSet contenant les données.
     * @return Un objet Player.
     * @throws SQLException En cas d'erreur SQL.
     */
    private static Player buildPlayer(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject("id");
        UUID team_id = (UUID) rs.getObject("team_id");

        String email = rs.getString("email");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");

        int jerseyNumber = rs.getInt("jersey_number");
        Date birthDate = new Date(rs.getDate("birth_date").getTime());
        int heightCm = rs.getInt("height_cm");
        int weightKg = rs.getInt("weight_kg");

        boolean isActive = rs.getBoolean("is_active");
        Date createdAt = rs.getTimestamp("created_at");
        Date updatedAt = rs.getTimestamp("updated_at");

        String picture = rs.getString("picture");
        String nomCsv = rs.getString("nom_csv");

        return new Player(uuid, team_id, email, password, firstName, lastName, jerseyNumber, birthDate, heightCm, weightKg, isActive, createdAt, updatedAt, picture, nomCsv);
    }

}