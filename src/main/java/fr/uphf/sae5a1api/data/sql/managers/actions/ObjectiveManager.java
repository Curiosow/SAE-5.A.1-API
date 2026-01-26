package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Objective;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.Connection; // Import ajouté
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ObjectiveManager {

    public static final String OBJECTIVES_TABLE = "objectives";
    public static final String GET_ALL_OBJECTIVES = "SELECT * FROM " + OBJECTIVES_TABLE;

    // SQL pour insérer
    public static final String INSERT_OBJECTIVE = "INSERT INTO " + OBJECTIVES_TABLE +
            " (match_id, type, title, operator, target_value, status, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";

    // SQL pour supprimer
    public static final String DELETE_OBJECTIVE = "DELETE FROM " + OBJECTIVES_TABLE + " WHERE id = ?";

    // Lecture (Reste identique, utilise DatabaseExecutor)
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

    // --- CORRECTION ICI : Utilisation de JDBC standard au lieu de DatabaseExecutor.execute ---
    public static void createObjective(Objective obj) {
        // On récupère la connexion directement
        try (Connection connection = HikariConnector.get().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_OBJECTIVE)) {

            // Gestion du match_id (0 ou null -> NULL SQL)
            if (obj.getMatchId() > 0) {
                statement.setInt(1, obj.getMatchId());
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }

            statement.setString(2, obj.getType());
            statement.setString(3, obj.getTitle());
            statement.setString(4, obj.getOperator());
            statement.setInt(5, obj.getTargetValue());
            statement.setString(6, "pending"); // Statut par défaut "en attente"

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteObjective(int id) {
        try (Connection connection = HikariConnector.get().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_OBJECTIVE)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ---------------------------------------------------------------------------------------

    private static Objective buildObjective(ResultSet rs) throws SQLException {
        Objective objective = new Objective();
        objective.setId(rs.getInt("id"));
        objective.setMatchId(rs.getInt("match_id"));
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