package fr.uphf.sae5a1api.data.sql.managers.teams;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.teams.Team;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TeamsManager {

    public static final String TEAMS_TABLE = "teams";
    public static final String GET_ALL_TEAMS = "SELECT * FROM " + TEAMS_TABLE;

    public static List<Team> getAllTeams() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(TeamsManager.GET_ALL_TEAMS);

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
        Date created_ad = rs.getTimestamp("created_at");
        Date updated_ad = rs.getTimestamp("updated_at");
        String logo = rs.getString("logo");

        return new Team(id, coach_id, name, description, season, is_active, created_ad, updated_ad, logo);
    }

}
