package fr.uphf.sae5a1api.data.sql.managers.data;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.actions.RankedTeam;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RankingManager {

    public static final String CLASSEMENTS_TABLE = "classements";
    public static final String SAVE = "INSERT INTO " + CLASSEMENTS_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
            "poule_competition_id = EXCLUDED.poule_competition_id, structure_id = EXCLUDED.structure_id, " +
            "competition_engagement_equipe_libelle = EXCLUDED.competition_engagement_equipe_libelle, " +
            "classement_place = EXCLUDED.classement_place, classement_place_last_update = EXCLUDED.classement_place_last_update, " +
            "evolution = EXCLUDED.evolution, classement_point_total = EXCLUDED.classement_point_total, " +
            "classement_nbr_match_joue = EXCLUDED.classement_nbr_match_joue, classement_nbr_match_gagne = EXCLUDED.classement_nbr_match_gagne, " +
            "classement_nbr_match_nul = EXCLUDED.classement_nbr_match_nul, classement_nbr_match_perdu = EXCLUDED.classement_nbr_match_perdu, " +
            "classement_but_plus = EXCLUDED.classement_but_plus, classement_but_moins = EXCLUDED.classement_but_moins, classement_difference = EXCLUDED.classement_difference";
    public static final String GET_ALL_TEAMS = "SELECT * FROM " + CLASSEMENTS_TABLE;

    public static void save(RankedTeam rankedTeam) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(RankingManager.SAVE);

            statement.setInt(1, rankedTeam.getId());
            statement.setInt(2, rankedTeam.getPoule_competition_id());
            statement.setInt(3, rankedTeam.getStructure_id());
            statement.setString(4, rankedTeam.getCompetition_engagement_equipe_libelle());
            statement.setInt(5, rankedTeam.getClassement_place());
            statement.setTimestamp(6, new Timestamp(rankedTeam.getClassement_place_last_update().getTime()));
            statement.setInt(7, rankedTeam.getEvolution());
            statement.setInt(8, rankedTeam.getClassement_point_total());
            statement.setInt(9, rankedTeam.getClassement_nbr_match_joue());
            statement.setInt(10, rankedTeam.getClassement_nbr_match_gagne());
            statement.setInt(11, rankedTeam.getClassement_nbr_match_nul());
            statement.setInt(12, rankedTeam.getClassement_nbr_match_perdu());
            statement.setInt(13, rankedTeam.getClassement_but_plus());
            statement.setInt(14, rankedTeam.getClassement_but_moins());
            statement.setInt(15, rankedTeam.getClassement_difference());

            statement.executeUpdate();
        });
    }

    public static List<RankedTeam> getAllTeams() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(RankingManager.GET_ALL_TEAMS);

            List<RankedTeam> rankedTeams = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
                rankedTeams.add(buildRankedTeam(resultSet));

            return rankedTeams;
        });
    }

    private static RankedTeam buildRankedTeam(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int poule_competition_id = rs.getInt("poule_competition_id");
        int structure_id = rs.getInt("structure_id");
        String competition_engagement_equipe_libelle = rs.getString("competition_engagement_equipe_libelle");
        int classement_place = rs.getInt("classement_place");
        Date classement_place_last_update = rs.getTimestamp("classement_place_last_update");
        int evolution = rs.getInt("evolution");
        int classement_point_total = rs.getInt("classement_point_total");
        int classement_nbr_match_joue = rs.getInt("classement_nbr_match_joue");
        int classement_nbr_match_gagne = rs.getInt("classement_nbr_match_gagne");
        int classement_nbr_match_nul = rs.getInt("classement_nbr_match_nul");
        int classement_nbr_match_perdu = rs.getInt("classement_nbr_match_perdu");
        int classement_but_plus = rs.getInt("classement_but_plus");
        int classement_but_moins = rs.getInt("classement_but_moins");
        int classement_difference = rs.getInt("classement_difference");

        return new RankedTeam(id, poule_competition_id, structure_id, competition_engagement_equipe_libelle,
                classement_place, classement_place_last_update, evolution, classement_point_total,
                classement_nbr_match_joue, classement_nbr_match_gagne, classement_nbr_match_nul, classement_nbr_match_perdu,
                classement_but_plus, classement_but_moins, classement_difference);
    }

}