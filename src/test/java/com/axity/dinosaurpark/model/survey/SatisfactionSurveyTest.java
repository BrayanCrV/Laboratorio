package com.axity.dinosaurpark.model.survey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Verifica que las encuestas sean simples, pero no acepten puntuaciones absurdas.
 */
class SatisfactionSurveyTest {

    @Test
    void storesValidSurveyData() {
        SatisfactionSurvey survey = new SatisfactionSurvey(4, "VIP", 5);

        assertEquals(4, survey.getTouristId());
        assertEquals("VIP", survey.getEnclosureName());
        assertEquals(5, survey.getScore());
    }

    @Test
    void rejectsScoresOutsideOneToFiveRange() {
        assertThrows(IllegalArgumentException.class, () -> new SatisfactionSurvey(4, "VIP", 0));
        assertThrows(IllegalArgumentException.class, () -> new SatisfactionSurvey(4, "VIP", 6));
    }
}
