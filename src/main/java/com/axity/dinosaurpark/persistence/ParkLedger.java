package com.axity.dinosaurpark.persistence;

/**
 * Contrato pequeno para que las zonas registren dinero sin saber todavia si hay CSV o base de datos.
 */
public interface ParkLedger {

    void appendRevenue(RevenueRecord record);

    void appendExpense(ExpenseRecord record);

    void appendEvent(EventRecord record);
}
