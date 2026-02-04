package fr.uphf.sae5a1api.data.impl.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionHandball {

    // ACTION CONFIG
    private Long id;
    private int matchId;
    private UUID teamId; // NOUVEAU : ID de l'équipe (Sambre ou Adversaire)

    // ACTION DETAILS
    private String nom;
    private double position;
    private double duree;
    private String defense;
    private String resultat;
    private String defensePlus;
    private String joueuse;
    private String secteur;
    private String attaquePlacees;
    private String enclenchements06;
    private String lieuPb;
    private String passeD;
    private String repli;
    private String defenseMoins;
    private String enclenchementsTransiER;
    private String grandEspace;
    private String jets7m;
    private String enclenchements6c5;

    // NOUVEAUX CHAMPS ANALYTIQUES (Enrichissement)
    private String tempsFormat;      // ex: "14:30"
    private Integer miTemps;         // 1 ou 2
    private Boolean moneyTime;       // true si > 55min
    private String phaseJeu;         // "Attaque" ou "Défense"
    private Integer scoreSambre;     // Score cumulé
    private Integer scoreAdversaire; // Score cumulé

    @Override
    public String toString() {
        return "ActionHandball{" +
                "id=" + id +
                ", matchId=" + matchId +
                ", teamId=" + teamId +
                ", nom='" + nom + '\'' +
                ", position=" + position +
                ", tempsFormat='" + tempsFormat + '\'' +
                ", phaseJeu='" + phaseJeu + '\'' +
                ", score=" + scoreSambre + "-" + scoreAdversaire +
                ", duree=" + duree +
                ", defense='" + defense + '\'' +
                ", resultat='" + resultat + '\'' +
                ", defensePlus='" + defensePlus + '\'' +
                ", joueuse='" + joueuse + '\'' +
                ", secteur='" + secteur + '\'' +
                ", attaquePlacees='" + attaquePlacees + '\'' +
                ", enclenchements06='" + enclenchements06 + '\'' +
                ", lieuPb='" + lieuPb + '\'' +
                ", passeD='" + passeD + '\'' +
                ", repli='" + repli + '\'' +
                ", defenseMoins='" + defenseMoins + '\'' +
                ", enclenchementsTransiER='" + enclenchementsTransiER + '\'' +
                ", grandEspace='" + grandEspace + '\'' +
                ", jets7m='" + jets7m + '\'' +
                ", enclenchements6c5='" + enclenchements6c5 + '\'' +
                ", miTemps=" + miTemps +
                ", moneyTime=" + moneyTime +
                '}';
    }
}