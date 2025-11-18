package fr.uphf.sae5a1api.data.impl.actions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Match {

    private int id;
    private String rencontreId;
    private UUID teamId;
    private String adversaire;
    private LocalDate dateMatch;
    private String lieu;
}