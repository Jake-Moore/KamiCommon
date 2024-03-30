package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;

import static com.kamikazejam.kamicommon.util.nms.NmsVersionParser.normalizePackage;

@SuppressWarnings("unused")
public class NmsVersion {

    private static String nmsVersion = null;
    /**
     * Normalizes the package name to the format used by the NMS classes.
     * @return The nms version, Ex: "v1_8_R3" or "v1_19_R2"
     */
    @SneakyThrows
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
        formattedNms = NmsVersionParser.getFormattedNmsInternal(getNMSVersion());
        return formattedNms;
    }

    private static double formattedNmsDouble = -1;
    /**
     * Converts {@link #getFormattedNms()} into a double
     * For example 1.8.9 becomes 189, 1.16 becomes 1160, 1.16.3 becomes 1163
     * @return The nms version formatted as a double. 4 digits (major[1]minor[2]patch[1]), i.e. 1_16_5 (1165) for v1_16_R3
     */
    public static double getFormattedNmsDouble() {
        if (formattedNmsDouble != -1) { return formattedNmsDouble; }
        formattedNmsDouble = NmsVersionParser.getFormattedNmsDouble(getNMSVersion());
        return formattedNmsDouble;
    }

    private static Boolean isWineSpigot = null;
    public static boolean isWineSpigot() {
        if (isWineSpigot == null) {
            return isWineSpigot = Bukkit.getServer().getName().equals("WineSpigot");
        }
        return isWineSpigot;
    }

















    private static ItemText itemText = null;
    public static ItemText getItemText() {
        if (itemText != null) { return itemText; }
        itemText = getItemTextInternal();
        return itemText;
    }

    //TODO: Add new versions as they come out
    private static ItemText getItemTextInternal() {
        String version = NmsVersion.getNMSVersion();
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

    }





    private static ITeleporter teleporter = null;
    public static ITeleporter getTeleporter() {
        if (teleporter != null) { return teleporter; }
        teleporter = getTeleporterInternal();
        return teleporter;
    }

    //TODO: Add new versions as they come out
    private static ITeleporter getTeleporterInternal() {
        String version = NmsVersion.getNMSVersion();
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

    @SuppressWarnings("all")
    public static @Nullable ItemStack getItemInMainHand(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        // If pre 1.9, we don't have an offhand slot
        if (NmsVersion.getFormattedNmsDouble() < 1090) {
            return playerInventory.getItemInHand();
        }else {
            return playerInventory.getItemInMainHand();
        }
    }
}
