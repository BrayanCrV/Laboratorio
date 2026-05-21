package com.axity.dinosaurpark.model.worker;

/**
 * Tecnico del parque; mas adelante se conectara con planta electrica y vehiculos.
 */
public class Technician extends Worker {

    public Technician(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "TECHNICIAN";
    }
}
