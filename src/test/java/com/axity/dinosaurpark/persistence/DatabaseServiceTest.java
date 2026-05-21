package com.axity.dinosaurpark.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * Prueba que H2 y Liquibase realmente creen tablas y acepten registros.
 */
class DatabaseServiceTest {

    @Test
    void appendRevenueStoresRecordInDatabase() throws Exception {
        try (DatabaseService databaseService = new DatabaseService(testDbPath())) {
            databaseService.appendRevenue(new RevenueRecord(
                99L,
                "TICKET",
                25.0,
                1,
                "Lugar de arribo",
                LocalDateTime.now()
            ));

            assertEquals(1, countRows(databaseService, "revenues"));
            assertEquals("TICKET", readSingleValue(databaseService, "SELECT type FROM revenues"));
        }
    }

    @Test
    void appendExpenseStoresRecordInDatabase() throws Exception {
        try (DatabaseService databaseService = new DatabaseService(testDbPath())) {
            databaseService.appendExpense(new ExpenseRecord(
                99L,
                "POWER_REPAIR",
                500.0,
                "Reparacion de planta",
                LocalDateTime.now()
            ));

            assertEquals(1, countRows(databaseService, "expenses"));
            assertEquals("POWER_REPAIR", readSingleValue(databaseService, "SELECT type FROM expenses"));
        }
    }

    @Test
    void appendEventStoresRecordInDatabase() throws Exception {
        try (DatabaseService databaseService = new DatabaseService(testDbPath())) {
            databaseService.appendEvent(new EventRecord(
                7L,
                "APAGON_MASIVO",
                "Apagon en el parque",
                "Planta de energia",
                LocalDateTime.now()
            ));

            assertEquals(1, countRows(databaseService, "events"));
            assertEquals("APAGON_MASIVO", readSingleValue(databaseService, "SELECT event_name FROM events"));
        }
    }

    private String testDbPath() {
        return "mem:test-" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";
    }

    private int countRows(DatabaseService databaseService, String tableName) throws Exception {
        try (Statement statement = databaseService.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private String readSingleValue(DatabaseService databaseService, String sql) throws Exception {
        try (Statement statement = databaseService.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getString(1);
        }
    }
}
