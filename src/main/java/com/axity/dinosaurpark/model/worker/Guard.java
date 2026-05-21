package com.axity.dinosaurpark.model.worker;

import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.dinosaur.DinosaurStatus;
import java.util.List;

/**
 * Guardia del parque; su trabajo es devolver el control cuando algun dinosaurio se escapa.
 */
public class Guard extends Worker {

    public Guard(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "GUARD";
    }

    public void recaptureEscapedDinosaurs(List<Dinosaur> dinosaurs) {
        if (dinosaurs == null) {
            return;
        }

        dinosaurs.stream()
            .filter(dinosaur -> dinosaur.getStatus() == DinosaurStatus.ESCAPED)
            .forEach(Dinosaur::returnToEnclosure);
    }
}
