package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.ApiStatus.Obsolete;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO - java doc update (including all methods)<br>
 * TODO - move non-color related methods to new class StringUtil
 * A color translator for LEGACY Color Codes. Translates the alternate code &amp; into &sect;<br>
 * <br>
 * This translator supports basic color codes and formats (i.e. {@code &a}, {@code &l}, etc.) and hex colors codes in the format of {@code &#FFAA00} (1.16+ only)<br>
 * <br>
 * Use StringUtilP for methods with players (translating PAPI placeholders) (part of the spigot-utils module)
 */
@Obsolete
@SuppressWarnings("unused")
public class LegacyColors {
    @SuppressWarnings("all")
    public static final char COLOR_CHAR = '\u00A7';

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

    @Obsolete
    public static String t(String msg) {
        return t(msg, false);
    }

    @Obsolete
    public static List<String> t(List<String> msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    @Obsolete
    public static List<String> t(String... msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    @Obsolete
    public static String reverseT(String s) {
        return s.replace(COLOR_CHAR, '&');
    }

    @Obsolete
    public static List<String> reverseT(List<String> s) {
        List<String> reversed = new ArrayList<>();
        for (String str : s) {
            reversed.add(reverseT(str));
        }
        return reversed;
    }

    @Obsolete
    public static String[] reverseT(String[] s) {
        List<String> reversed = new ArrayList<>();
        for (String str : s) {
            reversed.add(reverseT(str));
        }
        return reversed.toArray(new String[0]);
    }

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
