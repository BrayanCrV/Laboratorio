package com.axity.dinosaurpark.zone;

/**
 * Tipos de experiencia disponibles para observar dinosaurios.
 */
public enum ExperienceType {
    BASIC(1, 3),
    PREMIUM(2, 4),
    VIP(3, 5);

    private final int minimumSurveyScore;
    private final int maximumSurveyScore;

    ExperienceType(int minimumSurveyScore, int maximumSurveyScore) {
        this.minimumSurveyScore = minimumSurveyScore;
        this.maximumSurveyScore = maximumSurveyScore;
    }

    public int randomSurveyScore(java.util.Random rng) {
        return rng.nextInt(minimumSurveyScore, maximumSurveyScore + 1);
    }
}
