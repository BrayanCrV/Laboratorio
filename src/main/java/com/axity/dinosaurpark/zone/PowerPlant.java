package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.persistence.ParkLedger;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.Getter;

/**
 * Planta electrica del parque; mantiene todo encendido hasta que falla o se queda sin energia.
 */
@Getter
public class PowerPlant implements ParkZone {

    private static final String NAME = "Planta de Energia";

    private final double initialEnergy;
    private final double consumptionPerStep;
    private final double failureProbability;
    private final double maintenanceCost;
    private final double repairCost;
    private double currentEnergy;
    private boolean operational = true;
    private long nextExpenseId = 1L;

    public PowerPlant(
        double initialEnergy,
        double consumptionPerStep,
        double failureProbability,
        double maintenanceCost,
        double repairCost
    ) {
        this.initialEnergy = initialEnergy;
        this.currentEnergy = initialEnergy;
        this.consumptionPerStep = consumptionPerStep;
        this.failureProbability = failureProbability;
        this.maintenanceCost = maintenanceCost;
        this.repairCost = repairCost;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean hasCapacity() {
        return false;
    }

    @Override
    public int getCurrentOccupancy() {
        return 0;
    }

    @Override
    public int getMaxCapacity() {
        return 0;
    }

    @Override
    public void enter(Tourist tourist) {
        // La planta no recibe turistas, pero conserva el contrato comun de zonas.
    }

    @Override
    public void exit(Tourist tourist) {
        // La planta no recibe turistas, pero conserva el contrato comun de zonas.
    }

    public void tick(Random rng, ParkLedger ledger) {
        if (!operational) {
            return;
        }

        currentEnergy = Math.max(0.0, currentEnergy - consumptionPerStep);
        if (currentEnergy == 0.0 || rng.nextDouble() < failureProbability) {
            triggerFailure();
        }
    }

    public void triggerFailure() {
        operational = false;
        currentEnergy = 0.0;
    }

    public void repair(ParkLedger ledger) {
        operational = true;
        currentEnergy = initialEnergy;
        ledger.appendExpense(new ExpenseRecord(
            nextExpenseId++,
            "POWER_REPAIR",
            repairCost,
            "Reparacion de la planta electrica",
            LocalDateTime.now()
        ));
    }

    public void performMaintenance(ParkLedger ledger) {
        ledger.appendExpense(new ExpenseRecord(
            nextExpenseId++,
            "POWER_MAINTENANCE",
            maintenanceCost,
            "Mantenimiento preventivo de la planta electrica",
            LocalDateTime.now()
        ));
    }
}
