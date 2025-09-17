package fr.uphf.sae5A1api.data.managers.users;

import fr.uphf.sae5A1api.data.users.Coach;
import fr.uphf.sae5A1api.data.HikariConnector;
import fr.uphf.sae5A1api.data.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class UserManager {

    // TABLES
    public static final String COACH_TABLE = "coaches";
    public static final String PLAYER_TABLE = "players";

    // REQUESTS
    public static final String GET_COACH_BY_MAIL = "SELECT * FROM " + COACH_TABLE + " where email = ?";

    public static Coach getByMail(String mail) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACH_BY_MAIL);
            statement.setString(1, mail);

            ResultSet rs = statement.executeQuery();
            return rs.next() ? buildCoach(rs) : null;
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
