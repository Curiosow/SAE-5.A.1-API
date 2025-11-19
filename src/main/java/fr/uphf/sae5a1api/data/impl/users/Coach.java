package fr.uphf.sae5a1api.data.impl.users;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class Coach extends User {


    public Coach(UUID uuid, String email, String password, String first_name, String last_name, boolean active, Date created_at, Date updated_at, UUID team_id) {
        super(uuid, email, password, first_name, last_name, active, created_at, updated_at, team_id);
    }
}
