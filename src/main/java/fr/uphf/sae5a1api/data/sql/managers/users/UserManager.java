package fr.uphf.sae5a1api.data.sql.managers.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5a1api.data.impl.users.Coach;
import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.users.Player;
import fr.uphf.sae5a1api.data.impl.users.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

public class UserManager {

    // TABLES
    public static final String COACH_TABLE = "coaches";
    public static final String PLAYER_TABLE = "players";

    // REQUESTS

    // Savers
    public static final String SAVE_COACH = "INSERT INTO " + COACH_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SAVE_PLAYER = "INSERT INTO (id, email, password, first_name, last_name, is_active, created_at, updated_at, team_id, jersey_number, birth_date, height_cm, weight_kg, picture, nom_csv) " + PLAYER_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?,?)";

    // Getters
    public static final String GET_MEMBER_BY_MAIL = "SELECT first_name, last_name, email, 'player' AS account_type FROM " + PLAYER_TABLE + " WHERE email = ? UNION ALL SELECT first_name, last_name, email, 'coach' AS account_type FROM " + COACH_TABLE + " WHERE email = ?";
    public static final String GET_COACH_BY_MAIL = "SELECT * FROM " + COACH_TABLE + " where email = ?";
    public static final String GET_PLAYER_BY_MAIL = "SELECT * FROM " + PLAYER_TABLE + " where email = ?";

    // Global getters
    public static final String GET_MEMBERS = "SELECT first_name, last_name, email, 'player' AS account_type FROM " + PLAYER_TABLE + " UNION ALL SELECT first_name, last_name, email, 'coach' AS account_type FROM " + COACH_TABLE;
    public static final String GET_PLAYERS = "SELECT p.*, t.name AS team_name FROM " + PLAYER_TABLE + " p JOIN teams t ON p.team_id = t.id ";
    public static final String GET_COACHES = "SELECT * FROM " + COACH_TABLE;

    // Updaters
    public static final String UPDATE_INFORMATION = "UPDATE ? SET ? = ? WHERE email = ?";

    // ---- IMPLEMENTATION ---- \\

    // Saver
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

            if(user instanceof Player player) {
                statement.setObject(9, player.getTeam_id());
                statement.setInt(10, player.getJersey_number());
                statement.setDate(11, new java.sql.Date(player.getBirth_date().getTime()));
                statement.setInt(12, player.getHeight_cm());
                statement.setInt(13, player.getWeight_kg());
                statement.setString(14, player.getPicture_url());
                statement.setString(15, player.getNom_csv());
            }

            statement.executeUpdate();
        });
    }

    // [GLOBALS] GETTERS
    public static void getMembers(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_MEMBERS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

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

    public static void getPlayer(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_PLAYERS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    public static void getCoach(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACHES);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    // Updaters
    public static void updateInformation(boolean coach, String type, Object newValue, String email) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_INFORMATION);
            statement.setString(1, coach ? COACH_TABLE : PLAYER_TABLE);
            statement.setString(2, type);

            if(newValue instanceof String)
                statement.setString(3, (String) newValue);
            if(newValue instanceof Integer)
                statement.setInt(3, (Integer) newValue);
            if(newValue instanceof Boolean)
                statement.setBoolean(3, (Boolean) newValue);
            if(newValue instanceof Date)
                statement.setDate(3, new java.sql.Date(((Date) newValue).getTime()));


            statement.setString(4, email);
        });
    }

    // Other
    public static User login(String email, String rawPassword) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACH_BY_MAIL);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                if(BCrypt.verifyer().verify(rawPassword.toCharArray(), resultSet.getString("password")).verified)
                    return buildCoach(resultSet);
                else
                    return null;
            } else {
                statement = connection.prepareStatement(GET_PLAYER_BY_MAIL);
                statement.setString(1, email);
                resultSet = statement.executeQuery();
                if(resultSet.next()) {
                    System.out.println(resultSet.getString("first_name"));
                    if(BCrypt.verifyer().verify(rawPassword.toCharArray(), resultSet.getString("password")).verified)
                        return buildPlayer(resultSet);
                    else
                        return null;
                } else
                    return null;
            }

        });
    }

    // Builders
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
