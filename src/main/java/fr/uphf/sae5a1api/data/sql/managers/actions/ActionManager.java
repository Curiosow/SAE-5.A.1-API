package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.ActionHandball;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.util.UUID;

public class ActionManager {

    private static final String EVENEMENTS_TABLE = "evenements";
    private static final String SAVE = "INSERT INTO " + EVENEMENTS_TABLE + " (match_id, nom, position, duree, defense, resultat, defenseplus, joueuse, secteur, attaqueplacees, enclenchements06, lieupb, passed, repli, defensemoins, enclenchementstransier, grandespace, jets7m, enclenchements6c5) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static void saveAction(ActionHandball action) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(ActionManager.SAVE);

            statement.setObject(1, action.getMatchId());
            statement.setString(2, action.getNom());
            statement.setDouble(3, action.getPosition());
            statement.setDouble(4, action.getDuree());
            statement.setString(5, action.getDefense());
            statement.setString(6, action.getResultat());
            statement.setString(7, action.getDefensePlus());
            statement.setString(8, action.getJoueuse());
            statement.setString(9, action.getSecteur());
            statement.setString(10, action.getAttaquePlacees());
            statement.setString(11, action.getEnclenchements06());
            statement.setString(12, action.getLieuPb());
            statement.setString(13, action.getPasseD());
            statement.setString(14, action.getRepli());
            statement.setString(15, action.getDefenseMoins());
            statement.setString(16, action.getEnclenchementsTransiER());
            statement.setString(17, action.getGrandEspace());
            statement.setString(18, action.getJets7m());
            statement.setString(19, action.getEnclenchements6c5());

            statement.executeUpdate();
        });
    }
}
