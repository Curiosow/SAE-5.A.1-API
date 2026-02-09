package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EvenementManager {

    public static final String EVENEMENTS_TABLE = "evenements";
    public static final String GET_ALL_EVENEMENTS = "SELECT * FROM " + EVENEMENTS_TABLE;

    // REQUÊTE INSERT (ORDRE CRUCIAL)
    public static final String INSERT_SQL = "INSERT INTO " + EVENEMENTS_TABLE + " (" +
            "match_id, team_id, nom, position, duree, defense, resultat, defenseplus, joueuse, secteur, " +
            "attaqueplacees, enclenchements06, lieupb, passed, repli, defensemoins, " +
            "enclenchementstransier, grandespace, jets7m, enclenchements6c5, " +
            "temps_format, mi_temps, money_time, phase_jeu, score_sambre, score_adversaire" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    public static List<Evenement> getAllEvenements() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(GET_ALL_EVENEMENTS);
            List<Evenement> evenements = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) evenements.add(buildEvenement(resultSet));
            return evenements;
        });
    }

    public static void save(Evenement ev) {
        DatabaseExecutor.executeQuery(HikariConnector.get(), conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
            int i = 1;

            // 1. Identifiants
            ps.setInt(i++, ev.getMatchId());
            ps.setObject(i++, ev.getTeamId()); // UUID

            // 2. Données Brutes (String / Double)
            ps.setString(i++, ev.getNom());
            ps.setObject(i++, ev.getPosition());
            ps.setObject(i++, ev.getDuree());
            ps.setString(i++, ev.getDefense());
            ps.setString(i++, ev.getResultat());
            ps.setString(i++, ev.getDefenseplus());
            ps.setString(i++, ev.getJoueuse());
            ps.setString(i++, ev.getSecteur());
            ps.setString(i++, ev.getAttaqueplacees());
            ps.setString(i++, ev.getEnclenchements06());
            ps.setString(i++, ev.getLieupb());
            ps.setString(i++, ev.getPassed());
            ps.setString(i++, ev.getRepli());
            ps.setString(i++, ev.getDefensemoins());
            ps.setString(i++, ev.getEnclenchementstransier());
            ps.setString(i++, ev.getGrandespace());
            ps.setString(i++, ev.getJets7m());
            ps.setString(i++, ev.getEnclenchements6c5());

            // 3. Données Enrichies (Nouveaux champs)
            ps.setString(i++, ev.getTempsFormat());
            ps.setObject(i++, ev.getMiTemps());
            ps.setObject(i++, ev.getMoneyTime());
            ps.setString(i++, ev.getPhaseJeu());
            ps.setObject(i++, ev.getScoreSambre());
            ps.setObject(i++, ev.getScoreAdversaire());

            ps.execute();
            return true;
        });
    }

    private static Evenement buildEvenement(ResultSet rs) throws SQLException {
        Evenement ev = new Evenement();
        ev.setId(rs.getInt("id"));
        ev.setMatchId(rs.getInt("match_id"));
        ev.setTeamId((UUID) rs.getObject("team_id")); // UUID

        ev.setNom(rs.getString("nom"));
        ev.setPosition(rs.getDouble("position"));
        ev.setDuree(rs.getDouble("duree"));
        ev.setDefense(rs.getString("defense"));
        ev.setResultat(rs.getString("resultat"));
        ev.setDefenseplus(rs.getString("defenseplus"));
        ev.setJoueuse(rs.getString("joueuse"));
        ev.setSecteur(rs.getString("secteur"));
        ev.setAttaqueplacees(rs.getString("attaqueplacees"));
        ev.setEnclenchements06(rs.getString("enclenchements06"));
        ev.setLieupb(rs.getString("lieupb"));
        ev.setPassed(rs.getString("passed"));
        ev.setRepli(rs.getString("repli"));
        ev.setDefensemoins(rs.getString("defensemoins"));
        ev.setEnclenchementstransier(rs.getString("enclenchementstransier"));
        ev.setGrandespace(rs.getString("grandespace"));
        ev.setJets7m(rs.getString("jets7m"));
        ev.setEnclenchements6c5(rs.getString("enclenchements6c5"));

        // Enrichissement
        ev.setTempsFormat(rs.getString("temps_format"));
        ev.setMiTemps(rs.getObject("mi_temps") != null ? rs.getInt("mi_temps") : null);
        ev.setMoneyTime(rs.getObject("money_time") != null ? rs.getBoolean("money_time") : null);
        ev.setPhaseJeu(rs.getString("phase_jeu"));
        ev.setScoreSambre(rs.getObject("score_sambre") != null ? rs.getInt("score_sambre") : null);
        ev.setScoreAdversaire(rs.getObject("score_adversaire") != null ? rs.getInt("score_adversaire") : null);

        return ev;
    }
}