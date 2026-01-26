package fr.uphf.sae5a1api.data.impl.actions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Objective {

    private int id;
    private int matchId;
    private String type;
    private String title;
    private String metricKey;
    private String operator;
    private int targetValue;
    private int currentValue;
    private String status;
    private LocalDateTime createdAt;
}
