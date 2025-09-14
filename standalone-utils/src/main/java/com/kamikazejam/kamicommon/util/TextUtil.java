package com.kamikazejam.kamicommon.util;

import org.jetbrains.annotations.NotNull;

// TODO - java doc
@SuppressWarnings("unused")
public class TextUtil {
    // TODO - java doc
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
}
