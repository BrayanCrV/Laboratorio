package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;

/**
 * Puerta de entrada del parque: recibe turistas en fila y cobra sus boletos.
 */
public class ArrivalZone implements ParkZone {

    private static final String NAME = "Lugar de arribo";

    @Getter
    private final int maxCapacity;
    private final double ticketPrice;
    private final Queue<Tourist> waitingQueue = new ArrayDeque<>();
    private final List<Tourist> admittedTourists = new ArrayList<>();
    private long nextRevenueId = 1L;

    public ArrivalZone(int maxCapacity, double ticketPrice) {
        this.maxCapacity = maxCapacity;
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean hasCapacity() {
        return admittedTourists.size() < maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return admittedTourists.size();
    }

    public int getWaitingCount() {
        return waitingQueue.size();
    }

    @Override
    public void enter(Tourist tourist) {
        if (tourist != null) {
            waitingQueue.add(tourist);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        admittedTourists.remove(tourist);
    }

    public List<Tourist> processBatch(int batchSize, double discount, ParkLedger ledger) {
        List<Tourist> processed = new ArrayList<>();
        int safeBatchSize = Math.max(0, batchSize);

        while (processed.size() < safeBatchSize && !waitingQueue.isEmpty() && hasCapacity()) {
            Tourist tourist = waitingQueue.poll();
            admittedTourists.add(tourist);
            tourist.setStatus(TouristStatus.IN_PARK);
            tourist.recordVisit(NAME);

            double chargedAmount = applyDiscount(ticketPrice, discount);
            tourist.spend(chargedAmount);
            ledger.appendRevenue(new RevenueRecord(
                nextRevenueId++,
                "TICKET",
                chargedAmount,
                tourist.getId(),
                NAME,
                LocalDateTime.now()
            ));

            processed.add(tourist);
        }

        return processed;
    }

    private double applyDiscount(double amount, double discount) {
        double safeDiscount = Math.max(0.0, Math.min(1.0, discount));
        return amount * (1.0 - safeDiscount);
    }
}
