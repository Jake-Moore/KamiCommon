package com.kamikazejam.kamicommon.util.nms;

@SuppressWarnings("unused")
public class NmsVersionParser {

    /**
     * Converts the MC Version string into a 4-digit double
     * For example 1.8.9 becomes 1089, 1.16 becomes 1160, 1.16.3 becomes 1163
     * @param mcVer The MC version string (i.e. "1.20.4" or "1.8.8")
     * @return The nms version formatted as a double. 4 digits (major[1]minor[2]patch[1])
     */
    public static int getFormattedNmsInteger(String mcVer) {
        // Remove all . characters
        long num = mcVer.chars().filter(ch -> ch == '.').count();

        String s;
        if (num == 1) {
            // In this case 1.16 becomes 1160 and 1.8 becomes 1080
            String t = mcVer.replaceAll("\\.", "");
            if (t.length() == 2) {
                // In the case of 1.8 -> 1080
                s = mcVer.replaceAll("\\.", "0") + "0";
            }else {
                // In the case of 1.16 -> 1160
                s = t + "0";
            }
        }else if (num == 2) {
            // In this case 1.16.3 becomes 1163
            s = mcVer.replaceAll("\\.", "");
        }else {
            throw new IllegalArgumentException("Unknown version format: " + mcVer + " (expected a.b.c or a.b)");
        }

        return Integer.parseInt(s);
    }
}
