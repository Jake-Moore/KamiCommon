package com.kamikazejam.kamicommon.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Stand-ins for {@link String} methods added after Java 8.
 * <p>
 * KamiCommon's always-loaded modules target Java 8 so that 1.8.x servers can load them, which rules
 * out {@code String.repeat(int)} (Java 11) and {@code Jdk8.strip(String)} (Java 11). These are the same
 * operations, spelled for Java 8.
 * </p>
 * <p>
 * Internal. Use the JDK methods directly in your own code; this exists only because this library
 * cannot.
 * </p>
 */
@ApiStatus.Internal
public final class Jdk8 {

    private Jdk8() {}

    /**
     * {@code Jdk8.repeat(String, int)}, which is Java 11.
     *
     * @param value the string to repeat
     * @param count how many times, must not be negative
     * @return the repeated string
     */
    public static @NotNull String repeat(@NotNull String value, int count) {
        if (count < 0) { throw new IllegalArgumentException("count is negative: " + count); }
        if (count == 0 || value.isEmpty()) { return ""; }
        StringBuilder builder = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; i++) { builder.append(value); }
        return builder.toString();
    }

    /**
     * {@code Jdk8.strip(String)}, which is Java 11.
     * <p>
     * Deliberately not {@code trim()}: trim cuts everything at or below {@code U+0020}, which drops
     * control characters it should not and keeps Unicode spaces it should. This uses
     * {@link Character#isWhitespace(int)}, as {@code strip()} does.
     * </p>
     *
     * @param value the string to strip
     * @return the string without leading or trailing whitespace
     */
    public static @NotNull String strip(@NotNull String value) {
        int start = 0;
        int end = value.length();
        while (start < end && Character.isWhitespace(value.codePointAt(start))) {
            start += Character.charCount(value.codePointAt(start));
        }
        while (end > start && Character.isWhitespace(value.codePointBefore(end))) {
            end -= Character.charCount(value.codePointBefore(end));
        }
        return value.substring(start, end);
    }
}
