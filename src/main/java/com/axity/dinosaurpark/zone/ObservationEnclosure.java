package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.survey.SatisfactionSurvey;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.Getter;

/**
 * Recinto donde los turistas pagan por observar dinosaurios y dejan una encuesta.
 */
public class ObservationEnclosure implements ParkZone {

    @Getter
    private final String name;
    @Getter
    private final int maxCapacity;
    private final double entryFee;
    private final ExperienceType experienceType;
    @Getter
    private final List<Dinosaur> dinosaurs;
    private final List<Tourist> visitors = new ArrayList<>();
    private long nextRevenueId = 1L;

    public ObservationEnclosure(
        String name,
        int maxCapacity,
        double entryFee,
        ExperienceType experienceType,
        List<Dinosaur> dinosaurs
    ) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.entryFee = entryFee;
        this.experienceType = experienceType;
        this.dinosaurs = List.copyOf(dinosaurs);
    }

    @Override
    public boolean hasCapacity() {
        return visitors.size() < maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return visitors.size();
    }

    @Override
    public void enter(Tourist tourist) {
        if (tourist != null && hasCapacity()) {
            visitors.add(tourist);
            tourist.recordVisit(name);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        visitors.remove(tourist);
    }

    public Optional<SatisfactionSurvey> visit(Tourist tourist, Random rng, ParkLedger ledger) {
        if (tourist == null || rng == null || !hasCapacity()) {
            return Optional.empty();
        }

        enter(tourist);
        tourist.spend(entryFee);
        ledger.appendRevenue(new RevenueRecord(
            nextRevenueId++,
            "ENCLOSURE",
            entryFee,
            tourist.getId(),
            name,
            LocalDateTime.now()
        ));

        SatisfactionSurvey survey = conductSurvey(tourist, rng);
        exit(tourist);
        return Optional.of(survey);
    }

    public SatisfactionSurvey conductSurvey(Tourist tourist, Random rng) {
        return new SatisfactionSurvey(
            tourist.getId(),
            name,
            experienceType.randomSurveyScore(rng)
        );
    }
}
