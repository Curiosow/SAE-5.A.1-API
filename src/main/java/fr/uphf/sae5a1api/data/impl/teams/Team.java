package fr.uphf.sae5a1api.data.impl.teams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Team {

    private final UUID uuid;
    private UUID coach_id;
    private final String name;
    private String description;
    private String season;
    private final boolean is_active;
    private final Date create_at;
    private Date updated_at;
    private String logo;

}
