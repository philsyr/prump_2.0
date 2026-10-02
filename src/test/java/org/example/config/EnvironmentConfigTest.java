package org.example.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvironmentConfigTest {
    @ParameterizedTest
    @ValueSource(strings = {"BOT_TOKEN", "BOT_USERNAME", "DB_URL", "DB_USERNAME", "DB_PASSWORD"})
    void rejectsMissingConfigurationAndNamesOnlyTheMissingVariable(String name) {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> EnvironmentConfig.required(name, Map.of("OTHER_SETTING", "test-private-value")));

        assertEquals("Missing required environment variable: " + name, error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\r\n"})
    void rejectsBlankConfiguration(String value) {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> EnvironmentConfig.required("BOT_TOKEN", Map.of("BOT_TOKEN", value)));

        assertEquals("Missing required environment variable: BOT_TOKEN", error.getMessage());
    }

    @Test
    void preservesWhitespaceInANonBlankPassword() {
        String password = "  test-password-with-spaces  ";

        assertEquals(password,
                EnvironmentConfig.required("DB_PASSWORD", Map.of("DB_PASSWORD", password)));
    }

    @Test
    void returnsTheRequestedSetting() {
        Map<String, String> environment = Map.of(
                "BOT_TOKEN", "test-token",
                "BOT_USERNAME", "test_bot");

        assertEquals("test_bot", EnvironmentConfig.required("BOT_USERNAME", environment));
    }
}
