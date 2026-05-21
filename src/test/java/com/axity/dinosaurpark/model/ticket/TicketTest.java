package com.axity.dinosaurpark.model.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * Prueba el comprobante de entrada sin meter todavia la venta completa del parque.
 */
class TicketTest {

    @Test
    void storesTicketSaleData() {
        LocalDateTime issuedAt = LocalDateTime.of(2026, 5, 21, 12, 0);
        Ticket ticket = new Ticket(10L, 3, 25.0, "GENERAL", issuedAt);

        assertEquals(10L, ticket.getId());
        assertEquals(3, ticket.getTouristId());
        assertEquals(25.0, ticket.getPrice());
        assertEquals("GENERAL", ticket.getCategory());
        assertEquals(issuedAt, ticket.getIssuedAt());
    }
}
