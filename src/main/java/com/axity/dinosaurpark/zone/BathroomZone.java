package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import lombok.Getter;

/**
 * Zona de banos con cupo limitado y servicios extra como SPA.
 */
public class BathroomZone implements ParkZone {

    private static final String NAME = "Banos";

    @Getter
    private final int maxCapacity;
    private final int useDurationSteps;
    private final double spaPrice;
    private final double spaPurchaseProbability;
    private final Map<Tourist, Integer> occupants = new HashMap<>();
    private long nextRevenueId = 1L;

    public BathroomZone(int maxCapacity, int useDurationSteps, double spaPrice, double spaPurchaseProbability) {
        this.maxCapacity = maxCapacity;
        this.useDurationSteps = useDurationSteps;
        this.spaPrice = spaPrice;
        this.spaPurchaseProbability = spaPurchaseProbability;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean hasCapacity() {
        return occupants.size() < maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return occupants.size();
    }

    @Override
    public void enter(Tourist tourist) {
        if (tourist != null && hasCapacity()) {
            occupants.put(tourist, useDurationSteps);
            tourist.recordVisit(NAME);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        occupants.remove(tourist);
    }

    public boolean tryEnter(Tourist tourist, Random rng, ParkLedger ledger) {
        if (tourist == null || rng == null || !hasCapacity()) {
            return false;
        }

        enter(tourist);
        if (rng.nextDouble() < spaPurchaseProbability) {
            tourist.spend(spaPrice);
            ledger.appendRevenue(new RevenueRecord(
                nextRevenueId++,
                "SPA",
                spaPrice,
                tourist.getId(),
                NAME,
                LocalDateTime.now()
            ));
        }
        return true;
    }

    public void tick() {
        Iterator<Map.Entry<Tourist, Integer>> iterator = occupants.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Tourist, Integer> entry = iterator.next();
            int remainingSteps = entry.getValue() - 1;
            if (remainingSteps <= 0) {
                iterator.remove();
            } else {
                entry.setValue(remainingSteps);
            }
        }
    }
}
