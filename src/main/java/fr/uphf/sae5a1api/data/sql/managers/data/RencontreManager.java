package fr.uphf.sae5a1api.data.sql.managers.data;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.actions.Rencontre;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RencontreManager {

    public static final String RENCONTRES_TABLE = "rencontres";

    public static final String SAVE = "INSERT INTO " + RENCONTRES_TABLE + " VALUES (?, ?, ?, ?::date, ?::time, ?, ?, ?, ?, ?, ?, ?, ?, ?::date, ?::date, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
            "rencontres_id = EXCLUDED.rencontres_id, competition_id = EXCLUDED.competition_id, " +
            "rencontre_conclusion_info_date_match = EXCLUDED.rencontre_conclusion_info_date_match, " +
            "rencontre_conclusion_info_heure_match = EXCLUDED.rencontre_conclusion_info_heure_match, " +
            "competition_engagement_equipe_libelle_1 = EXCLUDED.competition_engagement_equipe_libelle_1, " +
            "structure_id_1 = EXCLUDED.structure_id_1, " +
            "competition_engagement_equipe_libelle_2 = EXCLUDED.competition_engagement_equipe_libelle_2, " +
            "structure_id_2 = EXCLUDED.structure_id_2, " +
            "rencontres_info_equipe1_score = EXCLUDED.rencontres_info_equipe1_score, " +
            "rencontres_info_equipe2_score = EXCLUDED.rencontres_info_equipe2_score, " +
            "rencontres_aller_id = EXCLUDED.rencontres_aller_id, " +
            "calendrier_journee_numero = EXCLUDED.calendrier_journee_numero, " +
            "calendrier_date_journee_debut = EXCLUDED.calendrier_date_journee_debut, " +
            "calendrier_date_journee_fin = EXCLUDED.calendrier_date_journee_fin, " +
            "equipement_nom_salle = EXCLUDED.equipement_nom_salle, " +
            "coordonnees_rue = EXCLUDED.coordonnees_rue, " +
            "ville_code_postal = EXCLUDED.ville_code_postal, " +
            "ville_libelle = EXCLUDED.ville_libelle, " +
            "rencontre_fdm_code = EXCLUDED.rencontre_fdm_code, " +
            "lien_fdme = EXCLUDED.lien_fdme, " +
            "channel = EXCLUDED.channel";

    public static final String GET_ALL_RENCONTRES = "SELECT * FROM " + RENCONTRES_TABLE;

    public static void save(Rencontre rencontre) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(RencontreManager.SAVE);

            statement.setInt(1, rencontre.getId());
            statement.setInt(2, rencontre.getRencontres_id());
            statement.setInt(3, rencontre.getCompetition_id());
            statement.setString(4, rencontre.getRencontre_conclusion_info_date_match());
            statement.setString(5, rencontre.getRencontre_conclusion_info_heure_match());
            statement.setString(6, rencontre.getCompetition_engagement_equipe_libelle_1());
            statement.setInt(7, rencontre.getStructure_id_1());
            statement.setString(8, rencontre.getCompetition_engagement_equipe_libelle_2());
            statement.setInt(9, rencontre.getStructure_id_2());
            statement.setInt(10, rencontre.getRencontres_info_equipe1_score());
            statement.setInt(11, rencontre.getRencontres_info_equipe2_score());
            statement.setString(12, rencontre.getRencontres_aller_id());
            statement.setInt(13, rencontre.getCalendrier_journee_numero());
            statement.setString(14, rencontre.getCalendrier_date_journee_debut());
            statement.setString(15, rencontre.getCalendrier_date_journee_fin());
            statement.setString(16, rencontre.getEquipement_nom_salle());
            statement.setString(17, rencontre.getCoordonnees_rue());
            statement.setString(18, rencontre.getVille_code_postal());
            statement.setString(19, rencontre.getVille_libelle());
            statement.setString(20, rencontre.getRencontre_fdm_code());
            statement.setString(21, rencontre.getLien_fdme());
            statement.setString(22, rencontre.getChannel());

            statement.executeUpdate();
        });
    }

    public static List<Rencontre> getAllRencontres() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(RencontreManager.GET_ALL_RENCONTRES);

            List<Rencontre> rencontres = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
                rencontres.add(buildRencontre(resultSet));

            return rencontres;
        });
    }

    private static Rencontre buildRencontre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int rencontres_id = rs.getInt("rencontres_id");
        int competition_id = rs.getInt("competition_id");
        String rencontre_conclusion_info_date_match = rs.getString("rencontre_conclusion_info_date_match");
        String rencontre_conclusion_info_heure_match = rs.getString("rencontre_conclusion_info_heure_match");
        String competition_engagement_equipe_libelle_1 = rs.getString("competition_engagement_equipe_libelle_1");
        int structure_id_1 = rs.getInt("structure_id_1");
        String competition_engagement_equipe_libelle_2 = rs.getString("competition_engagement_equipe_libelle_2");
        int structure_id_2 = rs.getInt("structure_id_2");
        int rencontres_info_equipe1_score = rs.getInt("rencontres_info_equipe1_score");
        int rencontres_info_equipe2_score = rs.getInt("rencontres_info_equipe2_score");
        String rencontres_aller_id = rs.getString("rencontres_aller_id");
        int calendrier_journee_numero = rs.getInt("calendrier_journee_numero");
        String calendrier_date_journee_debut = rs.getString("calendrier_date_journee_debut");
        String calendrier_date_journee_fin = rs.getString("calendrier_date_journee_fin");
        String equipement_nom_salle = rs.getString("equipement_nom_salle");
        String coordonnees_rue = rs.getString("coordonnees_rue");
        String ville_code_postal = rs.getString("ville_code_postal");
        String ville_libelle = rs.getString("ville_libelle");
        String rencontre_fdm_code = rs.getString("rencontre_fdm_code");
        String lien_fdme = rs.getString("lien_fdme");
        String channel = rs.getString("channel");

        return new Rencontre(id, rencontres_id, competition_id, rencontre_conclusion_info_date_match,
                rencontre_conclusion_info_heure_match, competition_engagement_equipe_libelle_1, structure_id_1,
                competition_engagement_equipe_libelle_2, structure_id_2, rencontres_info_equipe1_score,
                rencontres_info_equipe2_score, rencontres_aller_id, calendrier_journee_numero,
                calendrier_date_journee_debut, calendrier_date_journee_fin, equipement_nom_salle,
                coordonnees_rue, ville_code_postal, ville_libelle, rencontre_fdm_code, lien_fdme, channel);
    }
}