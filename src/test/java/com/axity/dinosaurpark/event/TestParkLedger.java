package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Ledger de prueba para eventos; evita traer una base de datos antes de tiempo.
 */
public class TestParkLedger implements ParkLedger {

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
