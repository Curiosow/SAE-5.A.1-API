package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.ActionHandball;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
// Ne pas oublier les imports pour les types
import java.sql.Types;

public class ActionManager {

    private static final String EVENEMENTS_TABLE = "evenements";

    // Mise à jour de la requête pour inclure team_id et les données enrichies
    private static final String SAVE = "INSERT INTO " + EVENEMENTS_TABLE + " (" +
            "match_id, team_id, nom, position, duree, defense, resultat, defenseplus, joueuse, secteur, " +
            "attaqueplacees, enclenchements06, lieupb, passed, repli, defensemoins, " +
            "enclenchementstransier, grandespace, jets7m, enclenchements6c5, " +
            "temps_format, mi_temps, money_time, phase_jeu, score_sambre, score_adversaire" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static void saveAction(ActionHandball action) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(ActionManager.SAVE);
            int i = 1;

            // 1. Identifiants
            statement.setInt(i++, action.getMatchId());
            statement.setObject(i++, action.getTeamId()); // UUID (Nouveau)

            // 2. Données standards
            statement.setString(i++, action.getNom());
            statement.setDouble(i++, action.getPosition());
            statement.setDouble(i++, action.getDuree());
            statement.setString(i++, action.getDefense());
            statement.setString(i++, action.getResultat());
            statement.setString(i++, action.getDefensePlus());
            statement.setString(i++, action.getJoueuse());
            statement.setString(i++, action.getSecteur());
            statement.setString(i++, action.getAttaquePlacees());
            statement.setString(i++, action.getEnclenchements06());
            statement.setString(i++, action.getLieuPb());
            statement.setString(i++, action.getPasseD());
            statement.setString(i++, action.getRepli());
            statement.setString(i++, action.getDefenseMoins());
            statement.setString(i++, action.getEnclenchementsTransiER());
            statement.setString(i++, action.getGrandEspace());
            statement.setString(i++, action.getJets7m());
            statement.setString(i++, action.getEnclenchements6c5());

            // 3. Données Enrichies (Nouveaux champs)
            statement.setString(i++, action.getTempsFormat());
            statement.setObject(i++, action.getMiTemps(), Types.INTEGER);
            statement.setObject(i++, action.getMoneyTime(), Types.BOOLEAN);
            statement.setString(i++, action.getPhaseJeu());
            statement.setObject(i++, action.getScoreSambre(), Types.INTEGER);
            statement.setObject(i++, action.getScoreAdversaire(), Types.INTEGER);

            statement.executeUpdate();
        });
    }
}