package fr.uphf.sae5a1api.data.impl.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Position {

    private final UUID id;
    private final UUID player_id;
    private final String name;
    private final String abrevation;
    private final String description;
    private final Date created_at;

}
