package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.tourist.Tourist;

/**
 * Contrato comun para las zonas que pueden recibir o liberar visitantes.
 */
public interface ParkZone {

    String getName();

    boolean hasCapacity();

    int getCurrentOccupancy();

    int getMaxCapacity();

    void enter(Tourist tourist);

    void exit(Tourist tourist);
}
