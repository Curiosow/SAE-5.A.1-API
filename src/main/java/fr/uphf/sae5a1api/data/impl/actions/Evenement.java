package fr.uphf.sae5a1api.data.impl.actions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Evenement {

    private int id;
    private int matchId; // Correspond à match_id
    private String nom;
    private Double position;
    private Double duree;
    private String defense;
    private String resultat;
    private String defenseplus;
    private String joueuse;
    private String secteur;
    private String attaqueplacees;
    private String enclenchements06;
    private String lieupb;
    private String passed;
    private String repli;
    private String defensemoins;
    private String enclenchementstransier;
    private String grandespace;
    private String jets7m;
    private String enclenchements6c5;
}