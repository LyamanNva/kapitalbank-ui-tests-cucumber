package dev.qa.kapitalbank.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Reads config.properties; a JVM system property with the same key always wins,
 * so CI can change browser, headless mode or URL without touching the file.
 */
public final class Config {

    private static final Properties FILE = load();

    private Config() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties is missing from the classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
        return properties;
    }

    public static String get(String key) {
        String value = System.getProperty(key, FILE.getProperty(key));
        if (value == null) {
            throw new IllegalStateException("Missing configuration key: " + key);
        }
        return value.trim();
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String browser() {
        return get("browser").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int windowWidth() {
        return Integer.parseInt(get("window.width"));
    }

    public static int windowHeight() {
        return Integer.parseInt(get("window.height"));
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds")));
    }
}
