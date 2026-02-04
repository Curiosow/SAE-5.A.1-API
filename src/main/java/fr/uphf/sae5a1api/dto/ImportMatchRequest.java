package fr.uphf.sae5a1api.dto;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import com.fasterxml.jackson.annotation.JsonProperty; // Important pour le mapping JSON
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ImportMatchRequest {

    private String rencontreId;

    // C'est ce champ qui récupère l'ID de l'équipe envoyé par le front
    private UUID teamId;

    private String adversaire;
    private String dateMatch;
    private String lieu;

    // La liste des événements du CSV
    private List<Evenement> events;
}