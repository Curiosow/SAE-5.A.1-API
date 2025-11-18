package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchManager {

    public static final String MATCHS_TABLE = "matchs";
    public static final String GET_ALL_MATCHS = "SELECT * FROM " + MATCHS_TABLE;


    public static List<Match> getAllMatchs() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(MatchManager.GET_ALL_MATCHS);

            List<Match> matchs = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
                matchs.add(buildMatch(resultSet));
            return matchs;
        });
    }

    private static Match buildMatch(ResultSet rs) throws SQLException {
        Match match = new Match();

        match.setId(rs.getInt("id"));
        match.setTeamId((UUID) rs.getObject("teamId"));
        match.setAdversaire(rs.getString("adversaire"));
        Date sqlDate = rs.getDate("dateMatch");
        if (sqlDate != null) {
            match.setDateMatch(sqlDate.toLocalDate());
        }
        match.setLieu(rs.getString("lieu"));

        return match;
    }
}