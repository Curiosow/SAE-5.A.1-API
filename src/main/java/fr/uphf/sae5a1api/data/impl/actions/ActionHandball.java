package fr.uphf.sae5a1api.data.impl.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ActionHandball {

    // ACTION CONFIG
    private Long id;
    private UUID matchId;

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

    @Override
    public String toString() {
        return "ActionHandball{" +
                "id=" + id +
                ", matchId=" + matchId +
                ", nom='" + nom + '\'' +
                ", position=" + position +
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
                '}';
    }

}

