package com.axity.dinosaurpark.config;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Carga la configuracion del parque una sola vez y la comparte con el resto del sistema.
 */
public final class ParkConfig {

    private static ParkConfig instance;
    private final Map<String, Object> values;

    private ParkConfig() {
        this.values = loadYaml();
    }

    public static synchronized ParkConfig getInstance() {
        if (instance == null) {
            instance = new ParkConfig();
        }
        return instance;
    }

    public int getInt(String key, int defaultValue) {
        Object value = findValue(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        Object value = findValue(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String getString(String key, String defaultValue) {
        Object value = findValue(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    public int getTotalSteps() {
        return getInt("simulation.totalSteps", 100);
    }

    public int getArrivalBatchSize() {
        return getInt("simulation.arrivalBatchSize", 5);
    }

    private Map<String, Object> loadYaml() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("park.yaml")) {
            if (input == null) {
                throw new IllegalStateException("No se encontro park.yaml en src/main/resources");
            }

            Yaml yaml = new Yaml();
            Object loaded = yaml.load(input);
            if (loaded instanceof Map<?, ?> loadedMap) {
                return castToStringObjectMap(loadedMap);
            }
            return Collections.emptyMap();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar park.yaml", e);
        }
    }

    private Object findValue(String key) {
        String[] parts = key.split("\\.");
        Object current = values;

        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = map.get(part);
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToStringObjectMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }

    static void resetForTesting() {
        instance = null;
    }
}
