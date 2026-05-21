package com.axity.dinosaurpark.model.survey;

import lombok.Getter;
import lombok.ToString;

/**
 * Opinion breve de un turista despues de visitar un recinto de observacion.
 */
@Getter
@ToString
public class SatisfactionSurvey {

    private final int touristId;
    private final String enclosureName;
    private final int score;

    public SatisfactionSurvey(int touristId, String enclosureName, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("La encuesta solo acepta puntuaciones de 1 a 5");
        }
        this.touristId = touristId;
        this.enclosureName = enclosureName;
        this.score = score;
    }
}
