package fr.uphf.sae5a1api.data.impl.users;

import java.util.Date;
import java.util.UUID;


public class Coach extends User {

    public Coach(UUID uuid, String email, String password, String first_name, String last_name, boolean active, Date created_at, Date updated_at) {
        super(uuid, email, password, first_name, last_name, active, created_at, updated_at);
    }
}
