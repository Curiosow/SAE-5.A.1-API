package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Objective;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ObjectiveManager {

    public static final String OBJECTIVES_TABLE = "objectives";

    // --- REQUÊTES SQL ---
    public static final String GET_ALL_OBJECTIVES = "SELECT * FROM " + OBJECTIVES_TABLE;

    public static final String INSERT_OBJECTIVE = "INSERT INTO " + OBJECTIVES_TABLE +
            " (match_id, type, title, metric_key, operator, target_value, current_value, status, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

    public static final String DELETE_OBJECTIVE = "DELETE FROM " + OBJECTIVES_TABLE + " WHERE id = ?";

    public static final String UPDATE_STATUS = "UPDATE " + OBJECTIVES_TABLE + " SET status = ? WHERE id = ?";


    // --- LECTURE ---
    public static List<Objective> getAllObjectives() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(GET_ALL_OBJECTIVES);
            List<Objective> objectives = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                objectives.add(buildObjective(resultSet));
            return objectives;
        });
    }

    // --- CRÉATION ---
    public static void createObjective(Objective obj) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(INSERT_OBJECTIVE);

            // Gestion Match ID (0 ou >0)
            if (obj.getMatchId() > 0) {
                statement.setInt(1, obj.getMatchId());
            } else {
                statement.setNull(1, Types.INTEGER);
            }

            statement.setString(2, obj.getType());
            statement.setString(3, obj.getTitle());
            // metric_key (peut être vide)
            statement.setString(4, obj.getMetricKey() != null ? obj.getMetricKey() : "");
            statement.setString(5, obj.getOperator());
            statement.setInt(6, obj.getTargetValue());
            statement.setInt(7, 0); // current_value (0 par défaut)
            statement.setString(8, "pending"); // status

            statement.executeUpdate();
        });
    }

    // --- MISE À JOUR STATUT (NOUVEAU) ---
    public static void updateStatus(int id, String status) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(UPDATE_STATUS);
            statement.setString(1, status);
            statement.setInt(2, id);
            statement.executeUpdate();
        });
    }

    // --- SUPPRESSION ---
    public static void deleteObjective(int id) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(DELETE_OBJECTIVE);
            statement.setInt(1, id);
            statement.executeUpdate();
        });
    }

    // --- MAPPER SQL -> JAVA ---
    private static Objective buildObjective(ResultSet rs) throws SQLException {
        Objective objective = new Objective();
        objective.setId(rs.getInt("id"));

        int matchId = rs.getInt("match_id");
        if (!rs.wasNull()) {
            objective.setMatchId(matchId);
        } else {
            objective.setMatchId(0);
        }

        objective.setType(rs.getString("type"));
        objective.setTitle(rs.getString("title"));
        objective.setMetricKey(rs.getString("metric_key"));
        objective.setOperator(rs.getString("operator"));
        objective.setTargetValue(rs.getInt("target_value"));
        objective.setCurrentValue(rs.getInt("current_value"));
        objective.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            objective.setCreatedAt(createdAt.toLocalDateTime());
        }

        return objective;
    }
}