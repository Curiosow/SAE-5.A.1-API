package fr.uphf.sae5a1api.data.impl.actions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * POJO représentant la table 'matches'.
 */
@Getter
@Setter
@NoArgsConstructor // Requis par Lombok
public class Match {

    private UUID id;
    private UUID teamId; // L'ID de votre équipe (Sambre)
    private String adversaire;
    private LocalDate dateMatch;
    private String lieu;
    private Integer scoreTeam = 0; // Score de votre équipe
    private Integer scoreAdversaire = 0;
    // Les timestamps created_at/updated_at ne sont pas nécessaires ici
    // sauf si vous les avez dans votre table 'matches'.

    /**
     * Constructeur utilisé pour créer un nouveau match avant l'import.
     */
    public Match(UUID id, UUID teamId, String adversaire, LocalDate dateMatch, String lieu) {
        this.id = id;
        this.teamId = teamId;
        this.adversaire = adversaire;
        this.dateMatch = dateMatch;
        this.lieu = lieu;
    }

    public Match(UUID id, UUID teamId, String adversaire, LocalDate dateMatch, String lieu, int scoreTeam, int scoreAdversaire) {
        this.id = id;
        this.teamId = teamId;
        this.adversaire = adversaire;
        this.dateMatch = dateMatch;
        this.lieu = lieu;
        this.scoreTeam = scoreTeam;
        this.scoreAdversaire = scoreAdversaire;
    }
}
