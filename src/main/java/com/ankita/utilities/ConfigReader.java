package com.ankita.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {
    }

    private static void loadProperties() {
        String configFile = "config.properties";
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Unable to find configuration file: " + configFile);
            }
            PROPERTIES.load(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load configuration properties", exception);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }
}
