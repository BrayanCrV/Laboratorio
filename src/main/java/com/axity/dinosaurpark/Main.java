package com.axity.dinosaurpark;

import com.axity.dinosaurpark.config.ParkConfig;

/**
 * Punto de entrada temporal del parque; por ahora confirma que la configuracion carga bien.
 */
public class Main {

    public static void main(String[] args) {
        ParkConfig config = ParkConfig.getInstance();

        System.out.println("Parque Turistico de Dinosaurios");
        System.out.println("Nivel: Intermedio");
        System.out.println("Pasos configurados: " + config.getTotalSteps());
        System.out.println("Turistas configurados: " + config.getInt("tourists", 0));
        System.out.println("Archivo de configuracion: park.yaml");
    }
}
