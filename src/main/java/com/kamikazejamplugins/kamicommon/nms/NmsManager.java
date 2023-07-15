package com.kamikazejamplugins.kamicommon.nms;

import com.kamikazejamplugins.kamicommon.nms.block.*;
import com.kamikazejamplugins.kamicommon.nms.hoveritem.*;
import com.kamikazejamplugins.kamicommon.nms.teleport.*;
import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class NmsManager {
    private static String nmsVersion = null;
    /**
     * Normalizes the package name to the format used by the NMS classes.
     * @return The nms version, Ex: "v1_8_R3" or "v1_19_R2"
     */
    public static String getNMSVersion() {
        if (nmsVersion != null) { return nmsVersion; }

        String v = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = normalizePackage(v.substring(v.lastIndexOf('.') + 1));
        return nmsVersion;
    }


    private static String formattedNms = null;
    /**
     * Normalizes the package name to the friendly version typically used.
     * Note: If the same NMS version was used for more than 1 version, the latest version using that nms class is returned
     * For example 1.19.1 and 1.19.2 use the R1 class, if you are on either, it will return 1.19.2 always
     * @return The nms version, Ex: "1.8.3" or "1.19.3"
     */
    public static String getFormattedNms() {
        if (formattedNms != null) { return formattedNms; }
        formattedNms = getFormattedNmsInternal();
        return formattedNms;
    }

    //TODO: Add new versions as they come out
    private static String getFormattedNmsInternal() {
        String nms = getNMSVersion();
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
        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }


    private static double formattedNmsDouble = -1;
    /**
     * Converts {@link #getFormattedNms()} into a double
     * For example 1.8.9 becomes 189, 1.16 becomes 1160, 1.16.3 becomes 1163
     * @return The formatted version as a double for comparison
     */
    public static double getFormattedNmsDouble() {
        if (formattedNmsDouble != -1) { return formattedNmsDouble; }

        String version = getFormattedNms();
        // Remove all . characters
        long num = version.chars().filter(ch -> ch == '.').count();

        String s;
        if (num == 1) {
            // In this case 1.16 becomes 1160
            s = version.replaceAll("\\.", "0") + "0";
        }else if (num == 2) {
            // In this case 1.16.3 becomes 1163
            s = version.replaceAll("\\.", "");
        }else {
            throw new IllegalArgumentException("Unknown version format: " + version);
        }

        formattedNmsDouble = Double.parseDouble(s);
        return formattedNmsDouble;
    }

    private static String normalizePackage(String nms) {
        if (nms.startsWith("v_")) {
            return "v" + nms.substring(2);
        }else {
            return nms;
        }
    }


    private static ItemText itemText = null;
    public static ItemText getItemText() {
        if (itemText != null) { return itemText; }
        itemText = getItemTextInternal();
        return itemText;
    }

    //TODO: Add new versions as they come out
    private static ItemText getItemTextInternal() {
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
            case "v1_19_R3":
                return new ItemText_1_19_R3();
            case "v1_20_R1":
                return new ItemText_1_20_R1();
            default:
                Bukkit.getLogger().severe("[KamiCommon NmsManager] Unsupported version: " + version);
                return new ItemText_1_8_R1();
        }
    }




    private static IBlockUtil blockUtil = null;
    public static IBlockUtil getBlockUtil() {
        if (blockUtil != null) { return blockUtil; }
        blockUtil = getBlockUtilInternal();
        return blockUtil;
    }

    private static IBlockUtil getBlockUtilInternal() {
        //TODO: Add new versions as they come out
        switch(getNMSVersion()) {
            case "v1_8_R1":
                return new BlockUtil1_8_R1();
            case "v1_8_R2":
                return new BlockUtil1_8_R2();
            case "v1_8_R3":
                return new BlockUtil1_8_R3();
            case "v1_9_R1":
                return new BlockUtil1_9_R1();
            case "v1_9_R2":
                return new BlockUtil1_9_R2();
            case "v1_10_R1":
                return new BlockUtil1_10_R1();
            case "v1_11_R1":
                return new BlockUtil1_11_R1();
            case "v1_12_R1":
                return new BlockUtil1_12_R1();
            case "v1_13_R1":
                return new BlockUtil1_13_R1();
            case "v1_13_R2":
                return new BlockUtil1_13_R2();
            case "v1_14_R1":
                return new BlockUtil1_14_R1();
            case "v1_15_R1":
                return new BlockUtil1_15_R1();
            case "v1_16_R1":
                return new BlockUtil1_16_R1();
            case "v1_16_R2":
                return new BlockUtil1_16_R2();
            case "v1_16_R3":
                return new BlockUtil1_16_R3();

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
            case "v1_19_R3":
                return new BlockUtil1_19_R3();
            case "v1_20_R1":
                return new BlockUtil1_20_R1();
        }
        throw new IllegalArgumentException(getNMSVersion() + " isn't a know version");
    }





    private static ITeleporter teleporter = null;
    public static ITeleporter getTeleporter() {
        if (teleporter != null) { return teleporter; }
        teleporter = getTeleporterInternal();
        return teleporter;
    }

    //TODO: Add new versions as they come out
    private static ITeleporter getTeleporterInternal() {
        String version = NmsManager.getNMSVersion();
        switch (version) {
            case "v1_8_R1":
                return new Teleporter1_8_R1();
            case "v1_8_R2":
                return new Teleporter1_8_R2();
            case "v1_8_R3":
                return new Teleporter1_8_R3();
            case "v1_9_R1":
                return new Teleporter1_9_R1();
            case "v1_9_R2":
                return new Teleporter1_9_R2();
            case "v1_10_R1":
                return new Teleporter1_10_R1();
            case "v1_11_R1":
                return new Teleporter1_11_R1();
            case "v1_12_R1":
                return new Teleporter1_12_R1();
            case "v1_13_R1":
                return new Teleporter1_13_R1();
            case "v1_13_R2":
                return new Teleporter1_13_R2();
            case "v1_14_R1":
                return new Teleporter1_14_R1();
            case "v1_15_R1":
                return new Teleporter1_15_R1();
            case "v1_16_R1":
                return new Teleporter1_16_R1();
            case "v1_16_R2":
                return new Teleporter1_16_R2();
            case "v1_16_R3":
                return new Teleporter1_16_R3();
            case "v1_17_R1":
                return new Teleporter1_17_R1();
            case "v1_18_R1":
                return new Teleporter1_18_R1();
            case "v1_18_R2":
                return new Teleporter1_18_R2();
            case "v1_19_R1":
                return new Teleporter1_19_R1();
            case "v1_19_R2":
                return new Teleporter1_19_R2();
            case "v1_19_R3":
                return new Teleporter1_19_R3();
            case "v1_20_R1":
                return new Teleporter1_20_R1();
            default:
                Bukkit.getLogger().severe("[KamiCommon NmsManager] Unsupported version: " + version);
                return new Teleporter1_20_R1();
        }
    }
}
