package com.kamikazejam.kamicommon.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A standalone compatible class for translating strings with color codes
 * Use {@link StringUtilP} for methods with players (translating PAPI placeholders)
 */
@SuppressWarnings("unused")
public class StringUtil {
    @SuppressWarnings("all")
    public static final char COLOR_CHAR = '\u00A7';

    public static String t(String msg, boolean forceTranslateHex) {
        String s = translateAlternateColorCodes(msg);

        // For 1.16+ translate hex color codes as well
        if (forceTranslateHex || StringUtilBukkit.supportsHexCodes()) {
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

    public static String reverseT(String s) {
        return s.replace("§", "&");
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

    public static String IntegerToRomanNumeral(int input) {
        if (input < 1 || input > 3999)
            return "Invalid Roman Number Value";
        StringBuilder s = new StringBuilder();
        while (input >= 1000) {
            s.append("M");
            input -= 1000;        }
        while (input >= 900) {
            s.append("CM");
            input -= 900;
        }
        while (input >= 500) {
            s.append("D");
            input -= 500;
        }
        while (input >= 400) {
            s.append("CD");
            input -= 400;
        }
        while (input >= 100) {
            s.append("C");
            input -= 100;
        }
        while (input >= 90) {
            s.append("XC");
            input -= 90;
        }
        while (input >= 50) {
            s.append("L");
            input -= 50;
        }
        while (input >= 40) {
            s.append("XL");
            input -= 40;
        }
        while (input >= 10) {
            s.append("X");
            input -= 10;
        }
        while (input == 9) {
            s.append("IX");
            input -= 9;
        }
        while (input >= 5) {
            s.append("V");
            input -= 5;
        }
        while (input == 4) {
            s.append("IV");
            input -= 4;
        }
        while (input >= 1) {
            s.append("I");
            input -= 1;
        }
        return s.toString();
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
}
