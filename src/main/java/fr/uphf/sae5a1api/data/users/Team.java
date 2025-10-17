package fr.uphf.sae5a1api.data.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Team {

    private UUID uuid;
    private UUID coach_uuid;
    private String name;
    private String description;
    private String season;
    private boolean active;
    private boolean is_active;
    private Date created_at;
    private Date updated_at;


}
