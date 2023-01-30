package com.kamikazejamplugins.kamicommon.nms;

import com.kamikazejamplugins.kamicommon.nms.block.*;
import com.kamikazejamplugins.kamicommon.nms.hoveritem.*;
import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class NmsManager {
    /**
     * Normalizes the package name to the format used by the NMS classes.
     * @return The nms version, Ex: "v1_8_R3" or "v1_19_R2"
     */
    public static String getNMSVersion(){
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return normalizePackage(v.substring(v.lastIndexOf('.') + 1));
    }

    /**
     * Normalizes the package name to the friendly version typically used.
     * Note: If the same NMS version was used for more than 1 version, the latest version using that nms class is returned
     * For example 1.19.1 and 1.19.2 use the R1 class, if you are on either, it will return 1.19.2 always
     * @return The nms version, Ex: "1.8.3" or "1.19.3"
     */
    //TODO: Add new versions as they come out
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

    /**
     * Converts {@link #getFormattedNms()} into a double
     * For example 1.8.9 becomes 1.89, and so on
     * @return The formatted version as a double for comparison
     */
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




    public static IBlockUtil getBlockUtil() {
        //TODO: Add new versions as they come out

        switch(getNMSVersion()) {
            case "v1_8_R1":
                return new BlockUtil1_8_R1();
            case "v1_8_R2":
                return new BlockUtil1_8_R2();
            case "v1_8_R3":
                return new BlockUtil1_8_R3();

            // TODO come back to this eventually to make it more efficient
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1":
            case "v1_13_R1":
            case "v1_13_R2":
            case "v1_14_R1":
            case "v1_15_R1":
            case "v1_16_R1":
            case "v1_16_R2":
            case "v1_16_R3":
                return new BlockUtil1_9_to_1_16();

            // I think this works, not sure
            case "v1_17_R1":
                return new BlockUtil1_17_R1();
            case "v1_18_R1":
                return new BlockUtil1_18_R1();
            case "v1_18_R2":
                return new BlockUtil1_18_R2();
            case "v1_19_R1":
                return new BlockUtil1_19_R1();
            case "v1_19_R2":
                return new BlockUtil1_19_R2();
        }
        throw new IllegalArgumentException(getNMSVersion() + " isn't a know version");
    }


    //TODO: Add new versions as they come out
    public static ItemText getItemText() {
        String version = NmsManager.getNMSVersion();
        switch (version) {
            case "v1_8_R1":
                return new ItemText_1_8_R1();
            case "v1_8_R2":
                return new ItemText_1_8_R2();
            case "v1_8_R3":
                return new ItemText_1_8_R3();
            case "v1_9_R1":
                return new ItemText_1_9_R1();
            case "v1_9_R2":
                return new ItemText_1_9_R2();
            case "v1_10_R1":
                return new ItemText_1_10_R1();
            case "v1_11_R1":
                return new ItemText_1_11_R1();
            case "v1_12_R1":
                return new ItemText_1_12_R1();
            case "v1_13_R1":
                return new ItemText_1_13_R1();
            case "v1_13_R2":
                return new ItemText_1_13_R2();
            case "v1_14_R1":
                return new ItemText_1_14_R1();
            case "v1_15_R1":
                return new ItemText_1_15_R1();
            case "v1_16_R1":
                return new ItemText_1_16_R1();
            case "v1_16_R2":
                return new ItemText_1_16_R2();
            case "v1_16_R3":
                return new ItemText_1_16_R3();
            case "v1_17_R1":
                return new ItemText_1_17_R1();
            case "v1_18_R1":
                return new ItemText_1_18_R1();
            case "v1_18_R2":
                return new ItemText_1_18_R2();
            case "v1_19_R1":
                return new ItemText_1_19_R1();
            case "v1_19_R2":
                return new ItemText_1_19_R2();
            default:
                Bukkit.getLogger().severe("[KamiCommon NBTManager] Unsupported version: " + version);
                return new ItemText_1_8_R1();
        }
    }
}
