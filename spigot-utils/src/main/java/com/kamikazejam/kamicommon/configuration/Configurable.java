package com.kamikazejam.kamicommon.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a configuration holder that contains static configuration
 * values, getters, and setters for its enclosing class.
 * <p>
 * This annotation is typically applied to static nested classes that serve
 * as centralized configuration points, making them easily discoverable
 * through reflection or IDE searches.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class KamiFeature {
 *     @ConfigHolder
 *     public static class Config {
 *         public static int TIMEOUT = 5000;
 *         public static void setTimeout(int timeout) { TIMEOUT = timeout; }
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {
    // intentionally empty
}
