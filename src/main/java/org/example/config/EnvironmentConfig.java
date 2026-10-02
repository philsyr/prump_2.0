package org.example.config;

import java.util.Map;

/** Reads required configuration without exposing its values in validation errors. */
public final class EnvironmentConfig {
    private EnvironmentConfig() {
    }

    public static String required(String name) {
        return required(name, System.getenv());
    }

    static String required(String name, Map<String, String> environment) {
        String value = environment.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }
}
