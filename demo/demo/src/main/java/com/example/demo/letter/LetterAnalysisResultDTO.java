package com.example.demo.letter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString // Useful for logging during development
public class LetterAnalysisResultDTO {

    @JsonProperty("danger_score") // Map JSON field "danger_score" to this Java field
    private Float dangerScore;

    @JsonProperty("danger_label") // Map JSON field "danger_label" to this Java field
    private String dangerLabel;

    private String rationale; // Assuming "rationale" directly matches, otherwise use @JsonProperty

    @JsonProperty("false_positive_score") // Map JSON field "false_positive_score" to this Java field
    private Float falsePositiveScore;
}