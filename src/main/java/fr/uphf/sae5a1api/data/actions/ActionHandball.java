package fr.uphf.sae5a1api.data.actions; // (Votre package)

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

/**
 * Ce POJO représente une ligne de la table "evenements" de votre BDD.
 * (Il ne correspond plus au CSV, mais à la structure de la BDD)
 */
@Getter
@Setter
@NoArgsConstructor // Requis par Lombok
public class ActionHandball {

    private Long id; // L'ID de la table (auto-incrémenté)
    private UUID matchId;
    private UUID joueurId; // Peut être null

    private Long timestampMs;
    private Integer dureeActionMs;

    private String categorieAction; // Ancien "position"
    private String resultat;
    private String secteur;
    private String contexteDefense; // Ancien "defense"

    // Les champs "PIVOT" pour les détails
    private String detailAction;
    private String detailEnclenchement;

    /**
     * Constructeur complet utilisé par le Controller lors du parsing.
     */
    public ActionHandball(UUID matchId, UUID joueurId, Long timestampMs, Integer dureeActionMs,
                          String categorieAction, String resultat, String secteur,
                          String contexteDefense, String detailAction, String detailEnclenchement) {
        this.matchId = matchId;
        this.joueurId = joueurId;
        this.timestampMs = timestampMs;
        this.dureeActionMs = dureeActionMs;
        this.categorieAction = categorieAction;
        this.resultat = resultat;
        this.secteur = secteur;
        this.contexteDefense = contexteDefense;
        this.detailAction = detailAction;
        this.detailEnclenchement = detailEnclenchement;
    }
}

