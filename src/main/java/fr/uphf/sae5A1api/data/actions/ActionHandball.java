package fr.uphf.sae5A1api.data.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionHandball {

    private String position;
    private double temps;
    private double duree;
    private String joueuse;
    private String resultat;
    private String secteur;
    private String defense;
    private String commentaire;

    public ActionHandball(String position, double temps, double duree,
                          String joueuse, String resultat, String secteur,
                          String defense, String commentaire) {
        this.position = position;
        this.temps = temps;
        this.duree = duree;
        this.joueuse = joueuse;
        this.resultat = resultat;
        this.secteur = secteur;
        this.defense = defense;
        this.commentaire = commentaire;
    }

    @Override
    public String toString() {
        return "Action{" +
                "pos='" + position + '\'' +
                ", temps=" + temps +
                ", duree=" + duree +
                ", joueuse='" + joueuse + '\'' +
                ", resultat='" + resultat + '\'' +
                ", secteur='" + secteur + '\'' +
                ", defense='" + defense + '\'' +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }

}
