package com.axity.dinosaurpark.model.ticket;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;

/**
 * Comprobante sencillo de una entrada vendida a un turista.
 */
@Getter
@ToString
public class Ticket {

    private final long id;
    private final int touristId;
    private final double price;
    private final String category;
    private final LocalDateTime issuedAt;

    public Ticket(long id, int touristId, double price, String category, LocalDateTime issuedAt) {
        this.id = id;
        this.touristId = touristId;
        this.price = price;
        this.category = category;
        this.issuedAt = issuedAt;
    }
}
