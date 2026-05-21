package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Libro contable de prueba: guarda en memoria lo que mas adelante ira a base de datos.
 */
class InMemoryParkLedger implements ParkLedger {

    private final List<RevenueRecord> revenues = new ArrayList<>();
    private final List<ExpenseRecord> expenses = new ArrayList<>();
    private final List<EventRecord> events = new ArrayList<>();

    @Override
    public void appendRevenue(RevenueRecord record) {
        revenues.add(record);
    }

    @Override
    public void appendExpense(ExpenseRecord record) {
        expenses.add(record);
    }

    @Override
    public void appendEvent(EventRecord record) {
        events.add(record);
    }

    List<RevenueRecord> getRevenues() {
        return revenues;
    }

    List<ExpenseRecord> getExpenses() {
        return expenses;
    }

    List<EventRecord> getEvents() {
        return events;
    }
}
