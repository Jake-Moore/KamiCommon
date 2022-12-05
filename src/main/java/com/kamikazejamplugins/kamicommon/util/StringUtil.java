package com.kamikazejamplugins.kamicommon.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class StringUtil {
    public static String t(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> t(List<String> msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    public static List<String> t(String[] msgs) {
        List<String> translated = new ArrayList<>();
        for (String msg : msgs) {
            translated.add(t(msg));
        }
        return translated;
    }

    public static String r(String msg, String placeholder, String replacement) {
        return msg.replaceAll(Pattern.quote(placeholder), replacement);
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

    public static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
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
}
