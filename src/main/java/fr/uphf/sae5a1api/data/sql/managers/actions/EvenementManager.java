package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvenementManager {

    public static final String EVENEMENTS_TABLE = "evenements";
    public static final String GET_ALL_EVENEMENTS = "SELECT * FROM " + EVENEMENTS_TABLE;

    public static List<Evenement> getAllEvenements() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(GET_ALL_EVENEMENTS);
            List<Evenement> evenements = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) evenements.add(buildEvenement(resultSet));
            return evenements;
        });
    }

    // --- NOUVELLE MÉTHODE D'INSERTION ---
    public static void save(Evenement ev) {
        DatabaseExecutor.executeQuery(HikariConnector.get(), conn -> {
            String sql = "INSERT INTO " + EVENEMENTS_TABLE + " (" +
                    "match_id, nom, position, duree, defense, resultat, defenseplus, joueuse, secteur, " +
                    "attaqueplacees, enclenchements06, lieupb, passed, repli, defensemoins, " +
                    "enclenchementstransier, grandespace, jets7m, enclenchements6c5" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

            PreparedStatement ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setInt(i++, ev.getMatchId());
            ps.setString(i++, ev.getNom());
            ps.setDouble(i++, ev.getPosition());
            ps.setDouble(i++, ev.getDuree());
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

            ps.execute();
            return true; // On doit retourner quelque chose pour DatabaseExecutor
        });
    }

    private static Evenement buildEvenement(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setId(rs.getInt("id"));
        evenement.setMatchId(rs.getInt("match_id"));
        evenement.setNom(rs.getString("nom"));
        evenement.setPosition(rs.getDouble("position"));
        evenement.setDuree(rs.getDouble("duree"));
        evenement.setDefense(rs.getString("defense"));
        evenement.setResultat(rs.getString("resultat"));
        evenement.setDefenseplus(rs.getString("defenseplus"));
        evenement.setJoueuse(rs.getString("joueuse"));
        evenement.setSecteur(rs.getString("secteur"));
        evenement.setAttaqueplacees(rs.getString("attaqueplacees"));
        evenement.setEnclenchements06(rs.getString("enclenchements06"));
        evenement.setLieupb(rs.getString("lieupb"));
        evenement.setPassed(rs.getString("passed"));
        evenement.setRepli(rs.getString("repli"));
        evenement.setDefensemoins(rs.getString("defensemoins"));
        evenement.setEnclenchementstransier(rs.getString("enclenchementstransier"));
        evenement.setGrandespace(rs.getString("grandespace"));
        evenement.setJets7m(rs.getString("jets7m"));
        evenement.setEnclenchements6c5(rs.getString("enclenchements6c5"));
        return evenement;
    }
}