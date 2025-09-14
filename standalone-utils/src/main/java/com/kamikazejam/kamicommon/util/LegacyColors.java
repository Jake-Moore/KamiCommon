package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.ApiStatus.Obsolete;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A legacy color-code translator for Minecraft-style formatting codes.<br>
 * Translates the alternate code character {@code &} into the legacy section sign ({@code §}) and optionally expands 1.16+ hex color codes of the form {@code &#RRGGBB} into the §x§R§R§G§G§B§B sequence used by legacy clients.<br>
 * Notes:<br>
 * - This API is marked {@code @Obsolete}. Prefer modern text/color APIs where possible.<br>
 * - Basic legacy color/format codes supported: 0-9, a-f, k-o, r (case-insensitive), e.g. {@code &a}, {@code &l}, {@code &r}, etc.<br>
 * - Hex color support requires either {@code forceTranslateHex = true} or {@code BukkitAdapter.supportsHexCodes()} to be true.
 */
@Obsolete
@SuppressWarnings("unused")
public class LegacyColors {
    /**
     * The legacy color code character (section sign), {@code '\u00A7'}.
     */
    @SuppressWarnings("all")
    public static final char COLOR_CHAR = '\u00A7';
    /**
     * Case-insensitive pattern that matches legacy color and format codes to strip.<br>
     * Matches:<br>
     * - {@code §[0-9A-FK-OR]} (single legacy codes), and<br>
     * - {@code §x(§[0-9A-F0-9]){6}} (expanded hex color sequences).
     */
    public static final @NotNull Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]|§x(§[0-9A-F0-9]){6}");

    /**
     * Translates alternate color codes in a message from {@code &} to {@code §} and, if enabled or supported, converts hex colors of the form {@code &#RRGGBB} into the legacy expanded {@code §x§R§R§G§G§B§B} sequence.
     *
     * @param msg the input message (non-null expected)
     * @param forceTranslateHex if true, hex codes are translated regardless of {@code BukkitAdapter.supportsHexCodes()}
     * @return the translated message with legacy codes applied
     */
    @Obsolete
    public static String t(String msg, boolean forceTranslateHex) {
        String s = translateAlternateColorCodes(msg);

        // For 1.16+ translate hex color codes as well
        if (forceTranslateHex || BukkitAdapter.supportsHexCodes()) {
            Pattern hex = Pattern.compile("&(#[A-Fa-f0-9]{6})");
            Matcher matcher = hex.matcher(s);
            while (matcher.find()) {
                StringBuilder s2 = new StringBuilder(COLOR_CHAR + "x");
                for (char c : matcher.group(1).substring(1).toCharArray()) {
                    s2.append(COLOR_CHAR).append(c);
                }

                s = s.replace(matcher.group(), "" + s2);
            }
        }

        return s;
    }

    /**
     * Translates alternate color codes in a message from {@code &} to {@code §}.<br>
     * Hex colors of the form {@code &#RRGGBB} are translated only if supported by the runtime ({@code BukkitAdapter.supportsHexCodes()} is true).
     *
     * @param msg the input message (non-null expected)
     * @return the translated message with legacy codes applied
     */
    @Obsolete
    public static String t(String msg) {
        return t(msg, false);
    }

    /**
     * Translates alternate color codes for a list of messages using {@link #t(String)} behavior (hex translation only if supported).
     *
     * @param msgs list of input messages
     * @return a new list containing translated messages
     */
    @Obsolete
    public static List<String> t(List<String> msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    /**
     * Translates alternate color codes for an array of messages using
     * {@link #t(String)} behavior (hex translation only if supported).
     *
     * @param msgs array of input messages
     * @return a new list containing translated messages
     */
    @Obsolete
    public static List<String> t(String... msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    /**
     * Removes all legacy color and format codes from the provided string.<br>
     * Supports stripping single legacy codes and expanded hex sequences.
     *
     * @param s the input string, may be null
     * @return the input without legacy color/format codes, or null if input is null
     */
    @Obsolete
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static String strip(String s) {
        if (s == null) { return null; }
        return STRIP_COLOR_PATTERN.matcher(s).replaceAll("");
    }

    /**
     * Replaces the legacy section sign {@code §} with the alternate color code character {@code &} in a single string.
     *
     * @param s the input string
     * @return the string with {@code §} replaced by {@code &}
     */
    @Obsolete
    public static String reverseT(String s) {
        return s.replace(COLOR_CHAR, '&');
    }

    /**
     * Replaces the legacy section sign {@code §} with {@code &} in each string of the provided list.
     *
     * @param s a list of strings to transform
     * @return a new list with {@code §} replaced by {@code &} in each element
     */
    @Obsolete
    public static List<String> reverseT(List<String> s) {
        List<String> reversed = new ArrayList<>();
        for (String str : s) {
            reversed.add(reverseT(str));
        }
        return reversed;
    }

    /**
     * Replaces the legacy section sign {@code §} with {@code &} in each string of the provided array.
     *
     * @param s an array of strings to transform
     * @return a new array with {@code §} replaced by {@code &} in each element
     */
    @Obsolete
    public static String[] reverseT(String[] s) {
        List<String> reversed = new ArrayList<>();
        for (String str : s) {
            reversed.add(reverseT(str));
        }
        return reversed.toArray(new String[0]);
    }

    /**
     * Converts alternate color codes using {@code &} to legacy section sign codes using {@code §} for basic color/format codes.<br>
     * This method does not handle hex colors; use {@link #t(String, boolean)} for full translation including hex.<br>
     * Characters following {@code &} that are in {@code 0-9, a-f, k-o, r} (case-insensitive) are converted, and the code letter is lowercased to match legacy expectations.
     *
     * @param textToTranslate the string containing alternate color codes
     * @return a new string with {@code &X} sequences converted to {@code §x}
     */
    @Obsolete
    private static String translateAlternateColorCodes(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    /**
     * A simple class to safely check if the given classpath is bukkit-compatible & supports hex codes<br>
     * Note: All methods in this class should be error-free, they handle exceptions internally
     */
    private static class BukkitAdapter {
        private static Boolean supportsHexCodes = null;
        /**
         * @return if Bukkit is available and the server version supports hex codes
         */
        private static boolean supportsHexCodes() {
            if (supportsHexCodes == null) {
                try {
                    // Will throw an exception on standalone instances or servers without bukkit (e.g. Velocity)
                    Class.forName("org.bukkit.Bukkit");

                    // IFF we have bukkit access, then we can use the NmsManager to check the version
                    String mcVer = getMCVersion();
                    supportsHexCodes = NmsVersionParser.getFormattedNmsInteger(mcVer) >= 1160;

                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
                    supportsHexCodes = false;
                }
            }
            return supportsHexCodes;
        }

        private static String mcVersion = null;
        /**
         * Returns the MC version of the server (i.e. 1.8.8 or 1.20.4) - via reflection
         * @return The MC version, Ex: "1.8.8" or "1.20.4"
         */
        private static String getMCVersion() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            if (mcVersion != null) { return mcVersion; }

            Class<?> serverClass = Class.forName("org.bukkit.Bukkit");
            Method getServerMethod = serverClass.getDeclaredMethod("getServer");
            Object serverObject = getServerMethod.invoke(null);
            Method getBukkitVersionMethod = serverObject.getClass().getDeclaredMethod("getBukkitVersion");
            String bukkitVer = (String) getBukkitVersionMethod.invoke(serverObject);
            mcVersion = bukkitVer.split("-")[0]; // i.e. 1.20.4 or 1.8.8
            return mcVersion;
        }
    }
}
