package fr.uphf.sae5a1api.data.impl.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class Evenement {
    private int id;

    @JsonProperty("match_id") private int matchId;
    @JsonProperty("team_id") private UUID teamId;

    private String nom;
    private Double position;
    private Double duree;
    private String defense;
    private String resultat;

    @JsonProperty("defense_plus") private String defenseplus; // Match le JS defense_plus
    private String joueuse;
    private String secteur;

    @JsonProperty("attaque_placees") private String attaqueplacees;
    @JsonProperty("enclenchements_06") private String enclenchements06;
    @JsonProperty("lieu_pb") private String lieupb;

    private String passed;
    private String repli;

    @JsonProperty("defense_moins") private String defensemoins;
    @JsonProperty("enclenchements_transier") private String enclenchementstransier;
    @JsonProperty("grand_espace") private String grandespace;
    @JsonProperty("jets_7m") private String jets7m;
    @JsonProperty("enclenchements_6c5") private String enclenchements6c5;

    // NOUVEAUX
    @JsonProperty("temps_format") private String tempsFormat;
    @JsonProperty("mi_temps") private Integer miTemps;
    @JsonProperty("money_time") private Boolean moneyTime;
    @JsonProperty("phase_jeu") private String phaseJeu;
    @JsonProperty("score_sambre") private Integer scoreSambre;
    @JsonProperty("score_adversaire") private Integer scoreAdversaire;
}