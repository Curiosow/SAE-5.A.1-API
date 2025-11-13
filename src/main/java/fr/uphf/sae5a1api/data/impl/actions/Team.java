package fr.uphf.sae5a1api.data.impl.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor

public class Team {
    private final UUID id;
    private final UUID coach_id;
    private final String name;
    private final String description;
    private final String season;
    private final boolean is_active;
    private final Date created_at;
    private final Date updated_at;
    private final String logo;
}
