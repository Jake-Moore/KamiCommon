package com.kamikazejam.kamicommon.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;

/**
 * A standalone compatible class for translating strings with color codes
 * Use StringUtilP for methods with players (translating PAPI placeholders) (part of the spigot-utils module)
 */
@SuppressWarnings("unused")
public class StringUtil {
    @SuppressWarnings("all")
    public static final char COLOR_CHAR = '\u00A7';

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

    public static String t(String msg) {
        return StringUtil.t(msg, false);
    }

    public static List<String> t(List<String> msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    public static List<String> t(String... msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    public static String r(String msg, String placeholder, String replacement) {
        return msg.replace(placeholder, replacement);
    }

    public static List<String> r(List<String> msg, String placeholder, String replacement) {
        List<String> replaced = new ArrayList<>();
        for (String s : msg) {
            replaced.add(r(s, placeholder, replacement));
        }
        return replaced;
    }

    public static String[] r(String[] msg, String placeholder, String replacement) {
        List<String> replaced = new ArrayList<>();
        for (String s : msg) {
            replaced.add(r(s, placeholder, replacement));
        }
        return replaced.toArray(new String[0]);
    }

    public static String rt(String msg, String placeholder, String replacement) {
        return t(r(msg, placeholder, replacement));
    }

    public static List<String> rt(List<String> msg, String placeholder, String replacement) {
        return t(r(msg, placeholder, replacement));
    }

    public static String[] rt(String[] msg, String placeholder, String replacement) {
        return t(r(msg, placeholder, replacement)).toArray(new String[0]);
    }

    public static String listToString(List<String> list) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1) {
                string.append(list.get(i)).append("\n");
            }else {
                string.append(list.get(i));
            }
        }
        return string.toString();
    }

    public static String combine(List<String> parts, String between) {
        return combine(parts.toArray(new String[0]), between);
    }

    public static String combine(String[] parts, String between) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i < parts.length - 1) {
                sb.append(parts[i]).append(between);
            }else {
                sb.append(parts[i]);
            }
        }
        return sb.toString();
    }

    /**
     * @param string The String[] to split
     * @param start The index to start at (inclusive)
     * @param end The index to end at (exclusive)
     * @return The resulting String[]
     */
    public static String[] subList(String[] string, int start, int end) {
        List<String> list = new ArrayList<>(Arrays.asList(string));
        return list.subList(start, end).toArray(new String[0]);
    }

    public static String reverseT(String s) {
        return s.replace(COLOR_CHAR, '&');
    }

    public static List<String> reverseT(List<String> s) {
        List<String> reversed = new ArrayList<>();
        for (String str : s) {
            reversed.add(reverseT(str));
        }
        return reversed;
    }

    public static String[] reverseT(String[] s) {
        List<String> reversed = new ArrayList<>();
        for (String str : s) {
            reversed.add(reverseT(str));
        }
        return reversed.toArray(new String[0]);
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Converts an integer to a Roman Numeral<br>
     * Supports ONLY integers in the range [1, 3999] (inclusive)<br>
     * @throws IllegalArgumentException if the input is not in the range [1, 3999]
     * @return The Roman Numeral representation of the input integer
     */
    @NotNull
    public static String IntegerToRomanNumeral(int input) throws IllegalArgumentException {
        if (input < 1 || input > 3999) {
            throw new IllegalArgumentException("Input must be in the range [1, 3999]");
        }

        // Define Roman numeral mappings
        final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        final String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        
        StringBuilder result = new StringBuilder();
        
        // Convert to Roman numerals
        for (int i = 0; i < values.length; i++) {
            while (input >= values[i]) {
                result.append(symbols[i]);
                input -= values[i];
            }
        }
        
        return result.toString();
    }

    protected static String translateAlternateColorCodes(String textToTranslate) {
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
