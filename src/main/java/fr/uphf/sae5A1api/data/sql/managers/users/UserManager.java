package fr.uphf.sae5A1api.data.sql.managers.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5A1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5A1api.data.users.Coach;
import fr.uphf.sae5A1api.data.HikariConnector;
import fr.uphf.sae5A1api.data.users.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserManager {

    // TABLES
    public static final String COACH_TABLE = "coaches";
    public static final String PLAYER_TABLE = "players";

    // REQUESTS
    public static final String GET_COACH_BY_MAIL  = "SELECT * FROM " + COACH_TABLE + " where email = ?";
    public static final String SAVE_COACH         = "INSERT INTO " + COACH_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SAVE_PLAYER        = "INSERT INTO " + PLAYER_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
            } else
                return null;

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

}
