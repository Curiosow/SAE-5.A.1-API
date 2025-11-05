package fr.uphf.sae5a1api.data.impl.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Rencontre {

    private final int id;
    private final int rencontres_id;
    private final int competition_id;
    private final String rencontre_conclusion_info_date_match;
    private final String rencontre_conclusion_info_heure_match;
    private final String competition_engagement_equipe_libelle_1;
    private final int structure_id_1;
    private final String competition_engagement_equipe_libelle_2;
    private final int structure_id_2;
    private final int rencontres_info_equipe1_score;
    private final int rencontres_info_equipe2_score;
    private final String rencontres_aller_id;
    private final int calendrier_journee_numero;
    private final String calendrier_date_journee_debut;
    private final String calendrier_date_journee_fin;
    private final String equipement_nom_salle;
    private final String coordonnees_rue;
    private final String ville_code_postal;
    private final String ville_libelle;
    private final String rencontre_fdm_code;
    private final String lien_fdme;
    private final String channel;
}