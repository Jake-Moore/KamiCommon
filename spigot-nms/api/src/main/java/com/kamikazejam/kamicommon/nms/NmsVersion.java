package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

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
     * Converts {@link #getFormattedNms()} into a double (4 digits)
     * For example 1.8.9 becomes 1089, 1.16 becomes 1160, 1.16.3 becomes 1163
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
}
