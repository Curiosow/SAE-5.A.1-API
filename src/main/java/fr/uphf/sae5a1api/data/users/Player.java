package fr.uphf.sae5a1api.data.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class Player extends User {
    private UUID team_id;
    private Integer jersey_number;
    private Date birth_date;
    private Integer height_cm;
    private Integer weight_kg;

    public Player(UUID id, UUID team_id, String email, String password, String first_name, String last_name,
                  Integer jersey_number, Date birth_date, Integer height_cm, Integer weight_kg,
                  boolean is_active, Date created_at, Date updated_at) {
        super(id, email, password, first_name, last_name, is_active, created_at, updated_at);
        this.team_id = team_id;
        this.jersey_number = jersey_number;
        this.birth_date = birth_date;
        this.height_cm = height_cm;
        this.weight_kg = weight_kg;
    }
}

