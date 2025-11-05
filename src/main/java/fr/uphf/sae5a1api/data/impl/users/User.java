package fr.uphf.sae5a1api.data.impl.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public abstract class User {

    private UUID uuid;
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private boolean active;
    private Date created_at;
    private Date updated_at;

}
