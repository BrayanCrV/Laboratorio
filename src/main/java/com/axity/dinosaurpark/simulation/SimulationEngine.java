package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.config.ParkConfig;
import com.axity.dinosaurpark.event.BlackoutEvent;
import com.axity.dinosaurpark.event.DealsHourEvent;
import com.axity.dinosaurpark.event.DinosaurEscapeEvent;
import com.axity.dinosaurpark.event.SimulationEvent;
import com.axity.dinosaurpark.event.StormEvent;
import com.axity.dinosaurpark.event.VehicleFailureEvent;
import com.axity.dinosaurpark.model.dinosaur.CarnivoreDinosaur;
import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.dinosaur.HerbivoreDinosaur;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.worker.Guard;
import com.axity.dinosaurpark.model.worker.Technician;
import com.axity.dinosaurpark.model.worker.Worker;
import com.axity.dinosaurpark.monitoring.ParkMonitor;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.zone.ArrivalZone;
import com.axity.dinosaurpark.zone.BathroomZone;
import com.axity.dinosaurpark.zone.CentralHub;
import com.axity.dinosaurpark.zone.ExperienceType;
import com.axity.dinosaurpark.zone.ObservationEnclosure;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Motor principal de la simulacion: construye todo el estado del parque y ejecuta el loop de pasos.
 */
public class SimulationEngine {

    private final ParkConfig config;
    private ParkState lastState;

    public SimulationEngine(ParkConfig config) {
        this.config = config;
    }

    public double getLastTotalRevenue() {
        return lastState != null ? lastState.getTotalRevenue() : 0.0;
    }

    public int getLastVehicleCount() {
        return lastState != null ? lastState.getVehicles().size() : 0;
    }

    public List<Tourist> getLastTourists() {
        return lastState != null ? lastState.getTourists() : List.of();
    }

    public void run() {
        String dbPath = config.getString("db.path", "./data/parkdb");
        run(dbPath);
    }

    public void run(String dbPath) {
        try (DatabaseService db = new DatabaseService(dbPath)) {
            ParkState state = buildInitialState(db);
            executeSimulationLoop(state, db);
        }
    }

    private ParkState buildInitialState(DatabaseService db) {
        // Turistas
        int touristCount = config.getInt("tourists", 50);
        List<Tourist> tourists = new ArrayList<>();
        for (int i = 1; i <= touristCount; i++) {
            tourists.add(new Tourist(i, "Turista " + i));
        }

        // Dinosaurios
        int carnivoreCount = config.getInt("dinosaurs.carnivores", 5);
        int herbivoreCount = config.getInt("dinosaurs.herbivores", 15);
        List<Dinosaur> dinosaurs = new ArrayList<>();
        for (int i = 1; i <= carnivoreCount; i++) {
            dinosaurs.add(new CarnivoreDinosaur(i, "Carnivoro " + i, "T-Rex"));
        }
        for (int i = 1; i <= herbivoreCount; i++) {
            dinosaurs.add(new HerbivoreDinosaur(carnivoreCount + i, "Herbivoro " + i, "Triceratops"));
        }

        // Workers
        double dailySalary = config.getDouble("workers.dailySalary", 150.0);
        int guardCount = config.getInt("workers.guards", 3);
        int technicianCount = config.getInt("workers.technicians", 2);
        List<Worker> workers = new ArrayList<>();
        int workerId = 1;
        for (int i = 0; i < guardCount; i++) {
            workers.add(new Guard(workerId++, "Guardia " + (i + 1), dailySalary));
        }
        for (int i = 0; i < technicianCount; i++) {
            workers.add(new Technician(workerId++, "Tecnico " + (i + 1), dailySalary));
        }

        // Vehiculos
        int vehicleCount = config.getInt("vehicles.count", 4);
        int vehicleRepairSteps = config.getInt("vehicles.repairSteps", 5);
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 1; i <= vehicleCount; i++) {
            vehicles.add(new Vehicle(i, "Vehiculo " + i, vehicleRepairSteps));
        }

        // Zonas
        ArrivalZone arrivalZone = new ArrivalZone(
            config.getInt("arrival.maxCapacity", 30),
            config.getDouble("arrival.ticketPrice", 25.0)
        );

        CentralHub centralHub = new CentralHub(
            config.getInt("hub.maxCapacity", 100),
            config.getDouble("hub.souvenirPrice", 15.0),
            config.getDouble("hub.souvenirPurchaseProbability", 0.4)
        );

        BathroomZone bathroomZone = new BathroomZone(
            config.getInt("bathroom.maxCapacity", 10),
            config.getInt("bathroom.useDurationSteps", 3),
            config.getDouble("bathroom.spaPrice", 20.0),
            config.getDouble("bathroom.spaPurchaseProbability", 0.2)
        );

        // Repartir dinosaurios entre los 3 encierros
        List<Dinosaur> basicDinos = new ArrayList<>();
        List<Dinosaur> premiumDinos = new ArrayList<>();
        List<Dinosaur> vipDinos = new ArrayList<>();
        for (int i = 0; i < dinosaurs.size(); i++) {
            if (i % 3 == 0) basicDinos.add(dinosaurs.get(i));
            else if (i % 3 == 1) premiumDinos.add(dinosaurs.get(i));
            else vipDinos.add(dinosaurs.get(i));
        }

        ObservationEnclosure basicEnclosure = new ObservationEnclosure(
            "Recinto Basico",
            config.getInt("enclosure.basic.maxVisitors", 20),
            config.getDouble("enclosure.basic.entryFee", 10.0),
            ExperienceType.BASIC,
            basicDinos
        );

        ObservationEnclosure premiumEnclosure = new ObservationEnclosure(
            "Recinto Premium",
            config.getInt("enclosure.premium.maxVisitors", 12),
            config.getDouble("enclosure.premium.entryFee", 30.0),
            ExperienceType.PREMIUM,
            premiumDinos
        );

        ObservationEnclosure vipEnclosure = new ObservationEnclosure(
            "Recinto VIP",
            config.getInt("enclosure.vip.maxVisitors", 5),
            config.getDouble("enclosure.vip.entryFee", 75.0),
            ExperienceType.VIP,
            vipDinos
        );

        PowerPlant powerPlant = new PowerPlant(
            config.getDouble("powerplant.initialEnergy", 100.0),
            config.getDouble("powerplant.consumptionPerStep", 1.5),
            config.getDouble("powerplant.failureProbability", 0.05),
            config.getDouble("powerplant.maintenanceCost", 200.0),
            config.getDouble("powerplant.repairCost", 500.0)
        );

        Random rng = new Random();

        // Poner turistas en la fila de llegada
        for (Tourist tourist : tourists) {
            arrivalZone.enter(tourist);
        }

        return new ParkState(
            tourists, dinosaurs, vehicles, powerPlant, db,
            workers, arrivalZone, centralHub, bathroomZone,
            basicEnclosure, premiumEnclosure, vipEnclosure, rng
        );
    }

    private void executeSimulationLoop(ParkState state, DatabaseService db) {
        int totalSteps = config.getTotalSteps();
        int batchSize = config.getArrivalBatchSize();
        int monitoringInterval = config.getInt("monitoring.intervalSteps", 10);

        List<SimulationEvent> allEvents = createEvents();
        List<Guard> guards = state.getWorkers().stream()
            .filter(w -> w instanceof Guard)
            .map(w -> (Guard) w)
            .toList();
        List<Technician> technicians = state.getWorkers().stream()
            .filter(w -> w instanceof Technician)
            .map(w -> (Technician) w)
            .toList();

        ArrivalZone arrivalZone = state.getArrivalZone();
        CentralHub centralHub = state.getCentralHub();
        BathroomZone bathroomZone = state.getBathroomZone();
        ObservationEnclosure basicEnclosure = state.getBasicEnclosure();
        ObservationEnclosure premiumEnclosure = state.getPremiumEnclosure();
        ObservationEnclosure vipEnclosure = state.getVipEnclosure();
        Random rng = state.getRng();

        long nextExpenseId = 1L;

        for (int step = 0; step < totalSteps; step++) {
            state.setCurrentStep(step);
            state.clearActiveEvents();

            // A. Llegadas de turistas
            arrivalZone.processBatch(batchSize, state.getCurrentDiscount(), state);

            // B. Movimiento de turistas activos
            List<Tourist> activeTourists = state.getTourists().stream()
                .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
                .toList();

            for (Tourist tourist : activeTourists) {
                centralHub.visit(tourist, rng, state.getCurrentDiscount(), state);
                bathroomZone.tryEnter(tourist, rng, state);
                basicEnclosure.visit(tourist, rng, state);
                premiumEnclosure.visit(tourist, rng, state);
                vipEnclosure.visit(tourist, rng, state);
            }

            // C. Ticks de zonas y vehiculos
            bathroomZone.tick();
            state.getPowerPlant().tick(rng, state);
            for (Vehicle vehicle : state.getVehicles()) {
                vehicle.tick();
            }

            // D. Eventos probabilisticos
            checkAndFireEvents(allEvents, state, rng);

            // E. Workers actuan
            for (Guard guard : guards) {
                guard.recaptureEscapedDinosaurs(state.getDinosaurs());
            }
            for (Technician tech : technicians) {
                tech.repairIfNeeded(state.getPowerPlant(), state.getVehicles());
            }

            // E2. Salarios
            for (Guard guard : guards) {
                state.appendExpense(new ExpenseRecord(
                    nextExpenseId++, "SALARY", guard.getDailySalary(),
                    "Salario de " + guard.getName(),
                    LocalDateTime.now()
                ));
            }
            for (Technician tech : technicians) {
                state.appendExpense(new ExpenseRecord(
                    nextExpenseId++, "SALARY", tech.getDailySalary(),
                    "Salario de " + tech.getName(),
                    LocalDateTime.now()
                ));
            }

            // F. Monitor
            ParkMonitor.displaySnapshot(state, step, monitoringInterval);
        }

        // G. Cierre del parque: todos los turistas salen del parque
        for (Tourist tourist : state.getTourists()) {
            if (tourist.getStatus() != TouristStatus.ATTACKED) {
                tourist.setStatus(TouristStatus.EXITED);
            }
        }

        // H. Resumen final
        System.out.println("\n==================================================");
        System.out.println("  RESUMEN FINAL DE LA SIMULACION");
        System.out.println("==================================================");
        System.out.println("  Turistas que visitaron el parque: " + state.getTourists().size());
        System.out.println("  Ingresos totales:                 $" + String.format("%.2f", state.getTotalRevenue()));
        System.out.println("  Gastos totales:                   $" + String.format("%.2f", state.getTotalExpenses()));
        System.out.println("  Balance neto:                     $" + String.format("%.2f", state.getTotalRevenue() - state.getTotalExpenses()));
        System.out.println("==================================================");

        lastState = state;
    }

    private List<SimulationEvent> createEvents() {
        List<SimulationEvent> events = new ArrayList<>();
        events.add(new DinosaurEscapeEvent(config.getDouble("event.escape.probability", 0.05)));
        events.add(new BlackoutEvent(config.getDouble("event.blackout.probability", 0.03)));
        events.add(new StormEvent(config.getDouble("event.storm.probability", 0.04)));
        events.add(new DealsHourEvent(config.getDouble("event.deals.probability", 0.08)));
        events.add(new VehicleFailureEvent(config.getDouble("event.vehicleFailure.probability", 0.06)));
        return events;
    }

    private void checkAndFireEvents(List<SimulationEvent> events, ParkState state, Random rng) {
        for (SimulationEvent event : events) {
            if (rng.nextDouble() < event.getProbability()) {
                event.execute(state, rng);
                state.registerActiveEvent(event.getName());
            }
        }
    }
}
