package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Team;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TeamManager {

    public static final String TEAMS_TABLE = "teams";

    public static final String SAVE = "INSERT INTO " + TEAMS_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
            "coach_id = EXCLUDED.coach_id, name = EXCLUDED.name, description = EXCLUDED.description, " +
            "season = EXCLUDED.season, is_active = EXCLUDED.is_active, " +
            "created_at = EXCLUDED.created_at, updated_at = EXCLUDED.updated_at, logo = EXCLUDED.logo";

    public static final String GET_ALL_TEAMS = "SELECT * FROM " + TEAMS_TABLE;

    public static void save(Team team) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(TeamManager.SAVE);

            statement.setObject(1, team.getId());
            statement.setObject(2, team.getCoach_id());
            statement.setString(3, team.getName());
            statement.setString(4, team.getDescription());
            statement.setString(5, team.getSeason());
            statement.setBoolean(6, team.is_active());
            statement.setTimestamp(7, new Timestamp(team.getCreated_at().getTime()));
            statement.setTimestamp(8, new Timestamp(team.getUpdated_at().getTime()));
            statement.setString(9, team.getLogo());

            statement.executeUpdate();
        });
    }

    public static List<Team> getAllTeams() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(TeamManager.GET_ALL_TEAMS);

            List<Team> teams = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
                teams.add(buildTeam(resultSet));

            return teams;
        });
    }

    private static Team buildTeam(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID coach_id = (UUID) rs.getObject("coach_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String season = rs.getString("season");
        boolean is_active = rs.getBoolean("is_active");
        Date created_at = rs.getTimestamp("created_at");
        Date updated_at = rs.getTimestamp("updated_at");
        String logo = rs.getString("logo");

        return new Team(id, coach_id, name, description, season, is_active, created_at, updated_at, logo);
    }
}