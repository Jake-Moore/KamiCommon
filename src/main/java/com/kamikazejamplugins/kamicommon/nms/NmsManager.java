package com.kamikazejamplugins.kamicommon.nms;

import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class NmsManager {
    public static String getNMSVersion(){
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return normalizePackage(v.substring(v.lastIndexOf('.') + 1));
    }

    public static String getFormattedNms(){
        String nms = normalizePackage(getNMSVersion());
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
        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }

    public static double getFormattedNmsDouble(){
        String version = getFormattedNms();
        //Remove every period after the first one
        StringBuilder s = new StringBuilder();
        boolean found = false;
        for (char c : version.toCharArray()) {
            if (c == '.'){ if (found) { continue; } found = true; }
            s.append(c);
        }
        return Double.parseDouble(s.toString());
    }

    private static String normalizePackage(String nms) {
        if (nms.startsWith("v_")) {
            return "v" + nms.substring(2);
        }else {
            return nms;
        }
    }
}
