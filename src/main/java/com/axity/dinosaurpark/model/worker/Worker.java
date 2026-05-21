package com.axity.dinosaurpark.model.worker;

import lombok.Getter;
import lombok.ToString;

/**
 * Persona contratada por el parque; cada tipo de trabajador aporta una funcion distinta.
 */
@Getter
@ToString
public abstract class Worker {

    private final int id;
    private final String name;
    private final double dailySalary;

    protected Worker(int id, String name, double dailySalary) {
        this.id = id;
        this.name = name;
        this.dailySalary = dailySalary;
    }

    public abstract String getRole();
}
