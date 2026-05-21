# Laboratorio - Parque Turistico de Dinosaurios

Simulacion en Java 17 de un parque turistico de dinosaurios para el laboratorio de Bloque 4.

## Nivel

Intermedio.

El proyecto se esta construyendo con:

- Java 17
- Maven
- JUnit 5
- Mockito
- Lombok
- SnakeYAML
- JaCoCo con cobertura minima de 65%

## Configuracion

La configuracion principal vive en:

```text
src/main/resources/park.yaml
```

Se eligio YAML en lugar de `.properties` para que la configuracion sea mas clara y agrupada por dominio.

## Patrones de Diseno

- Singleton: `ParkConfig` carga `park.yaml` una sola vez y comparte esa configuracion con el sistema.
- Strategy: `SimulationEvent` define el contrato comun para eventos como escapes, apagones y tormentas.

## Ejecutar

```bash
mvn exec:java
```

## Probar

```bash
mvn test
```

El reporte de cobertura se genera en:

```text
target/site/jacoco/index.html
```

## Estado actual

Ya estan implementados:

- Configuracion con YAML
- Modelos principales de turistas, dinosaurios, trabajadores, tickets y encuestas
- Vehiculos de mantenimiento para el flujo intermedio
- Zonas principales del parque
- Registro abstracto de ingresos, gastos y eventos mediante `ParkLedger`
- Eventos base con patron Strategy

Pendiente para completar el nivel intermedio:

- H2 + Liquibase
- `DatabaseService`
- Eventos intermedios de ofertas y fallas de vehiculo
- Motor completo de simulacion
- Monitor del parque
