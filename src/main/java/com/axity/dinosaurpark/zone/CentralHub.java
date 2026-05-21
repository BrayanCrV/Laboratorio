package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

/**
 * Centro del parque: orienta a los visitantes y les ofrece souvenirs.
 */
public class CentralHub implements ParkZone {

    private static final String NAME = "Recinto Central";

    @Getter
    private final int maxCapacity;
    private final double souvenirPrice;
    private final double souvenirPurchaseProbability;
    private final List<Tourist> visitors = new ArrayList<>();
    private long nextRevenueId = 1L;

    public CentralHub(int maxCapacity, double souvenirPrice, double souvenirPurchaseProbability) {
        this.maxCapacity = maxCapacity;
        this.souvenirPrice = souvenirPrice;
        this.souvenirPurchaseProbability = souvenirPurchaseProbability;
    }

    @Override
    public String getName() {
        return NAME;
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
            tourist.recordVisit(NAME);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        visitors.remove(tourist);
    }

    public boolean visit(Tourist tourist, Random rng, double discount, ParkLedger ledger) {
        if (tourist == null || rng == null || !hasCapacity()) {
            return false;
        }

        enter(tourist);
        boolean boughtSouvenir = rng.nextDouble() < souvenirPurchaseProbability;
        if (boughtSouvenir) {
            double chargedAmount = applyDiscount(souvenirPrice, discount);
            tourist.spend(chargedAmount);
            ledger.appendRevenue(new RevenueRecord(
                nextRevenueId++,
                "SOUVENIR",
                chargedAmount,
                tourist.getId(),
                NAME,
                LocalDateTime.now()
            ));
        }
        exit(tourist);
        return true;
    }

    private double applyDiscount(double amount, double discount) {
        double safeDiscount = Math.max(0.0, Math.min(1.0, discount));
        return amount * (1.0 - safeDiscount);
    }
}
