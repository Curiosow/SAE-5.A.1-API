package fr.uphf.sae5a1api.data.impl.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Objective {

    private int id;

    @JsonProperty("match_id") // Mappe le JSON "match_id" -> variable matchId
    private int matchId;

    private String type;
    private String title;

    @JsonProperty("metric_key")
    private String metricKey;

    private String operator;

    @JsonProperty("target_value") // CRUCIAL : Mappe "target_value" -> targetValue
    private int targetValue;

    @JsonProperty("current_value")
    private int currentValue;

    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}