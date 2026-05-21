package com.axity.dinosaurpark.zone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.dinosaur.CarnivoreDinosaur;
import com.axity.dinosaurpark.model.survey.SatisfactionSurvey;
import com.axity.dinosaurpark.model.tourist.Tourist;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Revisa cobros, encuestas y capacidad de los recintos de observacion.
 */
class ObservationEnclosureTest {

    @Test
    void visitChargesEntryFeeAndReturnsSurvey() {
        ObservationEnclosure enclosure = new ObservationEnclosure(
            "Recinto VIP",
            2,
            75.0,
            ExperienceType.VIP,
            List.of(new CarnivoreDinosaur(1, "Rex", "Tyrannosaurus"))
        );
        Tourist tourist = new Tourist(1, "Ana");
        InMemoryParkLedger ledger = new InMemoryParkLedger();

        Optional<SatisfactionSurvey> survey = enclosure.visit(tourist, new Random(1), ledger);

        assertTrue(survey.isPresent());
        assertEquals(75.0, tourist.getMoneySpent());
        assertEquals(1, ledger.getRevenues().size());
        assertEquals(0, enclosure.getCurrentOccupancy());
        assertTrue(survey.get().getScore() >= 3);
        assertTrue(survey.get().getScore() <= 5);
    }

    @Test
    void basicSurveyScoreStaysInBasicRange() {
        ObservationEnclosure enclosure = new ObservationEnclosure(
            "Recinto Basico",
            2,
            10.0,
            ExperienceType.BASIC,
            List.of()
        );

        SatisfactionSurvey survey = enclosure.conductSurvey(new Tourist(1, "Ana"), new Random(2));

        assertTrue(survey.getScore() >= 1);
        assertTrue(survey.getScore() <= 3);
    }

    @Test
    void refusesVisitWhenFull() {
        ObservationEnclosure enclosure = new ObservationEnclosure(
            "Recinto Premium",
            1,
            30.0,
            ExperienceType.PREMIUM,
            List.of()
        );
        enclosure.enter(new Tourist(1, "Ana"));

        Optional<SatisfactionSurvey> survey = enclosure.visit(
            new Tourist(2, "Luis"),
            new Random(1),
            new InMemoryParkLedger()
        );

        assertTrue(survey.isEmpty());
    }
}
