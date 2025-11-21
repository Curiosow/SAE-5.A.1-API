package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Position;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PositionManager {

    // TABLES
    private static final String TABLE_NAME = "positions2";

    // GETTERS
    private static final String GET_ALL_POSITIONS = "SELECT * FROM " + TABLE_NAME;

    // ---- IMPLEMENTATION ---- \\

    // GETTERS
    public static List<Position> getAllPositions() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_ALL_POSITIONS);
            ResultSet resultSet = statement.executeQuery();

            List<Position> positions = new ArrayList<>();
            while (resultSet.next()) {
                positions.add(buildPosition(resultSet));
            }

            return positions;
        });
    }

    private static Position buildPosition(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID player_id = (UUID) rs.getObject("player_id");
        String name = rs.getString("name");
        String abreviation = rs.getString("abreviation");
        String description = rs.getString("description");
        Date createdAt = rs.getTimestamp("created_at");

        return new Position(id, player_id, name, abreviation, description, createdAt);
    }

}
