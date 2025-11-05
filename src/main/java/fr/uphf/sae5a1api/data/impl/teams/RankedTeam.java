package fr.uphf.sae5a1api.data.impl.teams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class RankedTeam {

    private final int id;
    private final int poule_competition_id;
    private final int structure_id;
    private final String competition_engagement_equipe_libelle;
    private final int classement_place;
    private final Date classement_place_last_update;
    private final int evolution;
    private final int classement_point_total;
    private final int classement_nbr_match_joue;
    private final int classement_nbr_match_gagne;
    private final int classement_nbr_match_nul;
    private final int classement_nbr_match_perdu;
    private final int classement_but_plus;
    private final int classement_but_moins;
    private final int classement_difference;

}
