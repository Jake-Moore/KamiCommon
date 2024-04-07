package com.kamikazejam.kamicommon.util.nms;

@SuppressWarnings("unused")
public class NmsVersionParser {

    //TODO: Add new versions as they come out
    public static String getFormattedNmsInternal(String nms) {
        switch(nms) {
            case "v1_8_R1":
                return "1.8";
            case "v1_8_R2":
                return "1.8.3";
            case "v1_8_R3":
                return "1.8.8";

            case "v1_9_R1":
                return "1.9.2";
            case "v1_9_R2":
                return "1.9.4";

            case "v1_10_R1":
                return "1.10.2";

            case "v1_11_R1":
                return "1.11.2";

            case "v1_12_R1":
                return "1.12.2";

            case "v1_13_R1":
                return "1.13";
            case "v1_13_R2":
                return "1.13.2";

            case "v1_14_R1":
                return "1.14.4";

            case "v1_15_R1":
                return "1.15.2";

            case "v1_16_R1":
                return "1.16.1";
            case "v1_16_R2":
                return "1.16.3";
            case "v1_16_R3":
                return "1.16.5";

            case "v1_17_R1":
                return "1.17.1";

            case "v1_18_R1":
                return "1.18.1";
            case "v1_18_R2":
                return "1.18.2";

            case "v1_19_R1":
                return "1.19.2";
            case "v1_19_R2":
                return "1.19.3";
            case "v1_19_R3":
                return "1.19.4";
            case "v1_20_R1":
                return "1.20.1";
            case "v1_20_R2":
                return "1.20.2";
            case "v1_20_R3":
                return "1.20.4";
        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }


    /**
     * Converts the Formatted Nms string into a double
     * For example 1.8.9 becomes 189, 1.16 becomes 1160, 1.16.3 becomes 1163
     * @return The nms version formatted as a double. 4 digits (major[1]minor[2]patch[1]), i.e. 1_16_5 (1165) for v1_16_R3
     */
    public static double getFormattedNmsDouble(String nms) {
        String version = getFormattedNmsInternal(nms);
        // Remove all . characters
        long num = version.chars().filter(ch -> ch == '.').count();

        String s;
        if (num == 1) {
            // In this case 1.16 becomes 1160 and 1.8 becomes 1080
            String t = version.replaceAll("\\.", "");
            if (t.length() == 2) {
                // In the case of 1.8 -> 1080
                s = version.replaceAll("\\.", "0") + "0";
            }else {
                // In the case of 1.16 -> 1160
                s = t + "0";
            }
        }else if (num == 2) {
            // In this case 1.16.3 becomes 1163
            s = version.replaceAll("\\.", "");
        }else {
            throw new IllegalArgumentException("Unknown version format: " + version);
        }

        return Double.parseDouble(s);
    }

    /**
     * Normalizes the nms version string to start with v (not v_)
     */
    public static String normalizePackage(String nms) {
        if (nms.startsWith("v_")) {
            return "v" + nms.substring(2);
        }else {
            return nms;
        }
    }
}
