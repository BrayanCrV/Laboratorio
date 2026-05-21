package com.axity.dinosaurpark.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * Servicio sencillo que abre H2, ejecuta Liquibase y guarda los registros del parque.
 */
public class DatabaseService implements ParkLedger, AutoCloseable {

    private final Connection connection;

    public DatabaseService(String dbPath) {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:" + dbPath, "sa", "");
            runLiquibase();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo inicializar la base de datos", e);
        }
    }

    @Override
    public void appendRevenue(RevenueRecord record) {
        String sql = """
            INSERT INTO revenues (type, amount, tourist_id, zone, timestamp)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, record.type());
            statement.setDouble(2, record.amount());
            statement.setInt(3, record.touristId());
            statement.setString(4, record.zone());
            statement.setTimestamp(5, Timestamp.valueOf(record.timestamp()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el ingreso", e);
        }
    }

    @Override
    public void appendExpense(ExpenseRecord record) {
        String sql = """
            INSERT INTO expenses (type, amount, description, timestamp)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, record.type());
            statement.setDouble(2, record.amount());
            statement.setString(3, record.description());
            statement.setTimestamp(4, Timestamp.valueOf(record.timestamp()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el gasto", e);
        }
    }

    @Override
    public void appendEvent(EventRecord record) {
        String sql = """
            INSERT INTO events (step, event_name, description, affected_entities, timestamp)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, record.step());
            statement.setString(2, record.eventName());
            statement.setString(3, record.description());
            statement.setString(4, record.affectedEntities());
            statement.setTimestamp(5, Timestamp.valueOf(record.timestamp()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el evento", e);
        }
    }

    Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo cerrar la conexion a la base de datos", e);
        }
    }

    private void runLiquibase() throws Exception {
        Database database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase(
            "db/changelog/db.changelog-master.xml",
            new ClassLoaderResourceAccessor(),
            database
        );
        liquibase.update(new Contexts());
    }
}
