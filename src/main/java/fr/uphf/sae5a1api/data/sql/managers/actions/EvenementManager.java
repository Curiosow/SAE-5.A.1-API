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
            PreparedStatement statement = data.prepareStatement(EvenementManager.GET_ALL_EVENEMENTS);

            List<Evenement> evenements = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
                evenements.add(buildEvenement(resultSet));
            return evenements;
        });
    }

    private static Evenement buildEvenement(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();

        evenement.setId(rs.getInt("id"));
        evenement.setMatchId(rs.getInt("match_id"));
        evenement.setNom(rs.getString("nom"));

        // getDouble renvoie 0.0 si null, mais c'est généralement acceptable pour numeric
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