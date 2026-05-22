# Parque Turistico de Dinosaurios

Simulacion secuencial en Java 17 de un parque turistico de dinosaurios. Nivel **Intermedio**.

## Herramientas Utilizadas

| Herramienta | Version | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Maven | - | Gestion de dependencias y build |
| JUnit 5 | 5.10.2 | Tests unitarios |
| Mockito | 5.11.0 | Mocks en tests |
| H2 | 2.2.224 | Base de datos embebida |
| Liquibase | 4.27.0 | Migraciones de base de datos |
| JaCoCo | 0.8.12 | Cobertura de tests (minimo 65%) |
| Lombok | 1.18.32 | Reduccion de boilerplate |
| SnakeYAML | 2.2 | Lectura de configuracion YAML |

## Instrucciones de Configuracion

La configuracion vive en `src/main/resources/park.yaml`. Parametros principales:

```yaml
simulation:
  totalSteps: 100          # Pasos de la simulacion
  arrivalBatchSize: 5       # Turistas por lote
tourists: 50                # Total de turistas
dinosaurs:
  carnivores: 5
  herbivores: 15
vehicles:
  count: 4                  # Vehiculos de mantenimiento
  repairSteps: 5            # Pasos para reparar vehiculo roto
db:
  path: ./data/parkdb      # Ruta de la base de datos H2
monitoring:
  intervalSteps: 10         # Imprimir monitor cada N pasos
```

## Ejecucion

```bash
# Compilar
mvn compile

# Correr la simulacion
mvn exec:java

# Ejecutar tests
mvn test

# Ver reporte de cobertura
# Abrir target/site/jacoco/index.html
```

## Explicacion General del Sistema

El sistema simula la operacion de un parque turistico de dinosaurios durante 100 pasos de tiempo. En cada paso:

1. **Llegadas** — Turistas entran al parque por lotes desde `ArrivalZone`
2. **Movimiento** — Turistas activos visitan `CentralHub`, `BathroomZone` y los `ObservationEnclosure`
3. **Ticks** — Zonas avanzan su estado interno; vehiculos se reparan; planta consume energia
4. **Eventos** — Se evaluan 5 eventos probabilisticos (escape, apagon, tormenta, ofertas, falla de vehiculo)
5. **Workers** — Guardias recapturan dinosaurios; tecnicos reparan la planta si hay vehiculo disponible
6. **Monitor** — Cada N pasos se imprime el estado del parque

Al finalizar todos los turistas salen y se muestra un resumen con ingresos, gastos y balance neto.

Los datos de ingresos, gastos y eventos se persisten en una base de datos H2 gestionada por Liquibase.

### Estructura del Proyecto

```
com.axity.dinosaurpark
├── config/          → ParkConfig (Singleton)
├── model/
│   ├── dinosaur/    → Dinosaur, CarnivoreDinosaur, HerbivoreDinosaur, DinosaurStatus
│   ├── tourist/     → Tourist, TouristStatus
│   ├── worker/      → Worker, Guard, Technician
│   ├── vehicle/     → Vehicle, VehicleStatus
│   ├── ticket/      → Ticket
│   └── survey/      → SatisfactionSurvey
├── zone/            → ParkZone, ArrivalZone, CentralHub, BathroomZone, PowerPlant, ObservationEnclosure
├── event/           → SimulationEvent (Strategy), DinosaurEscapeEvent, BlackoutEvent, StormEvent, DealsHourEvent, VehicleFailureEvent
├── persistence/     → ParkLedger, DatabaseService, RevenueRecord, ExpenseRecord, EventRecord
├── simulation/      → SimulationEngine, ParkState
├── monitoring/      → ParkMonitor
└── Main.java
```

## Patrones de Diseno

### 1. Singleton — ParkConfig

`ParkConfig` garantiza una unica instancia de configuracion cargada desde `park.yaml`. Todas las clases acceden a la misma configuracion via `ParkConfig.getInstance()`.

```
┌─────────────┐
│  ParkConfig  │ ← Singleton: una sola instancia
│ getInstance()│
└──────┬───────┘
       │ comparte configuracion
  ┌────┼────────────┐
  ▼    ▼            ▼
Engine Zonas      Eventos
```

### 2. Strategy — SimulationEvent

Cada evento implementa la interfaz `SimulationEvent` con su propio `execute()` y `getProbability()`. El Engine los recorre sin saber su tipo concreto, evaluando `rng.nextDouble() < event.getProbability()` para cada uno.

```
        ┌──────────────────┐
        │ SimulationEvent  │  ← Interfaz Strategy
        │ + execute()      │
        │ + getProbability()│
        └────────┬─────────┘
                 │ implementa
   ┌─────┬──────┼──────┬────────┐
   ▼     ▼      ▼      ▼        ▼
Escape Blackout Storm DealsHour VehicleFail
 5%     3%      4%     8%        6%
```

Flujo del Engine al disparar eventos:

```
Para cada step:
  ┌──────────────────────────────┐
  │ recorrer allEvents           │
  │   └→ rng.nextDouble()       │
  │      < prob? → event.execute│
  │      >= prob? → saltar      │
  └──────────────────────────────┘
```

### Diagrama de Flujo Principal

```
Main
 │
 ▼
SimulationEngine.run()
 │
 ├─ buildInitialState()
 │   ├─ Crear turistas, dinosaurios, workers, vehiculos
 │   ├─ Crear zonas (Arrival, Hub, Bathroom, Enclosures, PowerPlant)
 │   ├─ Turistas entran a ArrivalZone (fila)
 │   └─ Construir ParkState
 │
 └─ executeSimulationLoop()  ×100 pasos
     │
     ├─ A. Llegadas: arrivalZone.processBatch()
     │       └→ Turistas pasan de WAITING → IN_PARK
     │
     ├─ B. Movimiento: turistas IN_PARK visitan zonas
     │       ├→ CentralHub (souvenirs con probabilidad)
     │       ├→ BathroomZone (slots con duracion)
     │       └→ ObservationEnclosure (entrada + encuesta)
     │
     ├─ C. Ticks: bathroomZone.tick()
     │       powerPlant.tick()
     │       vehicle.tick() ×4
     │
     ├─ D. Eventos probabilisticos (5 eventos Strategy)
     │       ├→ Escape恐龙: dino escapa, posiblemente ataca turista
     │       ├→ Apagon: planta falla, gasto $2000
     │       ├→ Tormenta: turistas evacuados, gasto $500
     │       ├→ Ofertas: descuento 30% en boletos/souvenirs
     │       └→ Falla vehiculo: vehiculo AVAILABLE → BROKEN
     │
     ├─ E. Workers actuan
     │       ├→ Guardias: recapturar dinosaurios escapados
     │       └→ Tecnicos: reparar planta (necesitan vehiculo)
     │
     ├─ F. Monitor cada N pasos
     │
     └─ G. Cierre: turistas → EXITED, resumen final
```

### Diagrama de Persistencia

```
ParkState (implementa ParkLedger)
  │
  ├─ addRevenue()  ──→  DatabaseService.appendRevenue()
  │                       └→ INSERT INTO revenues
  │
  ├─ addExpense()  ──→  DatabaseService.appendExpense()
  │                       └→ INSERT INTO expenses
  │
  └─ appendEvent() ──→  DatabaseService.appendEvent()
                           └→ INSERT INTO events

Base de datos H2 (./data/parkdb)
  ├── revenues  (type, amount, tourist_id, zone, timestamp)
  ├── expenses  (type, amount, description, timestamp)
  └── events    (step, event_name, description, affected_entities, timestamp)

Liquibase: crea las 3 tablas automaticamente al arrancar
```

### Diagramas UML de Secuencia

Los diagramas estan en formato PlantUML (`diagrams/`). Puedes visualizarlos con:

- **VS Code**: Extension "PlantUML" (requiere Graphviz o PlantUML Server)
- **Online**: [plantuml.com](https://www.plantuml.com/plantuml) — copiar el contenido del archivo `.puml`
- **IntelliJ**: Plugin "PlantUML Integration"

| Archivo | Descripcion |
|---|---|
| `diagrams/sequence-main.puml` | Flujo completo de la simulacion (inicializacion + loop de 100 pasos) |
| `diagrams/sequence-escape-event.puml` | Evento de escape de dinosaurio y recaptura por guardia |
| `diagrams/sequence-blackout-event.puml` | Apagon masivo y reparacion por tecnico con vehiculo |
| `diagrams/sequence-deals-hour.puml` | Hora de ofertas con descuento del 30% |