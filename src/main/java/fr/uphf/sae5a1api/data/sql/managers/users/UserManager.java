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
    public static final String GET_COACH_BY_MAIL = "SELECT * FROM " + COACH_TABLE + " where email = ?";
    public static final String GET_PLAYER_BY_MAIL = "SELECT * FROM " + PLAYER_TABLE + " where email = ?";

    public static final String SAVE_COACH = "INSERT INTO " + COACH_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SAVE_PLAYER = "INSERT INTO " + PLAYER_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?,?)";
    public static final String GET_MEMBER = "SELECT first_name, last_name, email, 'player' AS account_type FROM " + PLAYER_TABLE + " UNION ALL SELECT first_name, last_name, email, 'coach' AS account_type FROM " + COACH_TABLE;
    public static final String GET_MEMBER_BY_MAIL = "SELECT first_name, last_name, email, 'player' AS account_type FROM " + PLAYER_TABLE + " WHERE email = ? UNION ALL SELECT first_name, last_name, email, 'coach' AS account_type FROM " + COACH_TABLE + " WHERE email = ?";
    public static final String GET_PLAYER = "SELECT p.*, t.name AS team_name FROM " + PLAYER_TABLE + " p JOIN teams t ON p.team_id = t.id ";
    public static final String GET_COACH = "SELECT * FROM " + COACH_TABLE;
    public static final String UPDATE_PLAYER_FIRSTNAME = "UPDATE " + PLAYER_TABLE + " SET first_name = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_LASTNAME = "UPDATE " + PLAYER_TABLE + " SET last_name = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_NUMBER = "UPDATE " + PLAYER_TABLE + " SET jersey_number = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_HEIGHT = "UPDATE " + PLAYER_TABLE + " SET height_cm = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_ACTIVE = "UPDATE " + PLAYER_TABLE + " SET is_active = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_EMAIL = "UPDATE " + PLAYER_TABLE + " SET email = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_BIRTHDATE = "UPDATE " + PLAYER_TABLE + " SET birth_date = ? WHERE email = ?";
    public static final String UPDATE_COACH_FIRSTNAME = "UPDATE " + COACH_TABLE + " SET first_name = ? WHERE email = ?";
    public static final String UPDATE_COACH_LASTNAME = "UPDATE " + COACH_TABLE + " SET last_name = ? WHERE email = ?";
    public static final String UPDATE_COACH_EMAIL = "UPDATE " + COACH_TABLE + " SET email = ? WHERE email = ?";
    public static final String UPDATE_COACH_ACTIVE = "UPDATE " + COACH_TABLE + " SET is_active = ? WHERE email = ?";


    public static void createUser(User user) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(user instanceof Coach ? SAVE_COACH : PLAYER_TABLE);

            statement.setObject(1, user.getUuid());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getFirst_name());
            statement.setString(5, user.getLast_name());
            statement.setBoolean(6, user.isActive());
            statement.setTimestamp(7, new Timestamp(user.getCreated_at().getTime()));
            statement.setTimestamp(8, new Timestamp(user.getUpdated_at().getTime()));
            statement.executeUpdate();
        });
    }

    public static void createPlayer(Player player) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(SAVE_PLAYER);

            statement.setObject(1, player.getUuid());
            statement.setObject(2, player.getTeam_id());
            statement.setString(3, player.getEmail());
            statement.setString(4, player.getPassword());
            statement.setString(5, player.getFirst_name());
            statement.setString(6, player.getLast_name());
            statement.setInt(7, player.getJersey_number());
            statement.setDate(8, new java.sql.Date(player.getBirth_date().getTime()));
            statement.setInt(9, player.getHeight_cm());
            statement.setInt(10, player.getWeight_kg());
            statement.setBoolean(11, player.isActive());
            statement.setTimestamp(12, new Timestamp(player.getCreated_at().getTime()));
            statement.setTimestamp(13, new Timestamp(player.getUpdated_at().getTime()));
            statement.setString(14, player.getPicture_url());
            statement.setString(15, player.getNom_csv());


            statement.executeUpdate();
        });
    }

    public static void getMembers(Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_MEMBER);
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

    public static void getPlayer(java.util.function.Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_PLAYER);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    public static void getCoach(java.util.function.Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACH);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    public static void updatePlayerFirstName(String email, String newFirstName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_FIRSTNAME);
            statement.setString(1, newFirstName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerLastName(String email, String newLastName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_LASTNAME);
            statement.setString(1, newLastName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerNumber(String email, int jerseyNumber) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_NUMBER);
            statement.setInt(1, jerseyNumber);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerHeight(String email, int heightCm) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_HEIGHT);
            statement.setInt(1, heightCm);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerActive(String email, boolean isActive) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_ACTIVE);
            statement.setBoolean(1, isActive);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerEmail(String oldEmail, String newEmail) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_EMAIL);
            statement.setString(1, newEmail);
            statement.setString(2, oldEmail);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerBirthDate(String email, java.util.Date birthDate) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_BIRTHDATE);
            statement.setDate(1, new java.sql.Date(birthDate.getTime()));
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updateCoachFirstName(String email, String newFirstName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_FIRSTNAME);
            statement.setString(1, newFirstName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updateCoachLastName(String email, String newLastName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_LASTNAME);
            statement.setString(1, newLastName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updateCoachEmail(String oldEmail, String newEmail) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_EMAIL);
            statement.setString(1, newEmail);
            statement.setString(2, oldEmail);
            statement.executeUpdate();
        });
    }

    public static void updateCoachActive(String email, boolean isActive) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_ACTIVE);
            statement.setBoolean(1, isActive);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }






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



    private static Coach buildCoach(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject("id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        boolean isActive = rs.getBoolean("is_active");
        Date createdAt = rs.getTimestamp("created_at");
        Date updatedAt = rs.getTimestamp("updated_at");

        return new Coach(uuid, email, password, firstName, lastName, isActive, createdAt, updatedAt);
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
