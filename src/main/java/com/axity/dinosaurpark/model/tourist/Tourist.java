package com.axity.dinosaurpark.model.tourist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa a una persona que visita el parque, gasta dinero y deja rastro de sus visitas.
 */
@Getter
@ToString
public class Tourist {

    private final int id;
    private final String name;
    @Setter
    private TouristStatus status = TouristStatus.WAITING;
    private double moneySpent;
    private final List<String> visitedZones = new ArrayList<>();

    public Tourist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void spend(double amount) {
        if (amount <= 0) {
            return;
        }
        moneySpent += amount;
    }

    public void recordVisit(String zoneName) {
        if (zoneName == null || zoneName.isBlank()) {
            return;
        }
        visitedZones.add(zoneName);
    }

    public List<String> getVisitedZones() {
        return Collections.unmodifiableList(visitedZones);
    }
}
