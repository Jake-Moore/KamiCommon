package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class NmsVersion {

    private static String mcVersion = null;
    /**
     * Returns the MC version of the server (i.e. 1.8.8 or 1.20.4)
     * Note: As of the removal of cb package relocation, the vX_XX_RX package is no longer valid, we must use MC version
     * @return The MC version, Ex: "1.8.8" or "1.20.4"
     */
    @SneakyThrows
    public static String getMCVersion() {
        if (mcVersion != null) { return mcVersion; }

        String bukkitVer = Bukkit.getServer().getBukkitVersion(); // i.e. 1.20.4-R0.1-SNAPSHOT
        mcVersion = bukkitVer.split("-")[0]; // i.e. 1.20.4
        return mcVersion;
    }

    private static int formattedNms = -1;
    /**
     * Converts {@link #getMCVersion()} into a double (4 digits)
     * For example 1.8.9 becomes 1089, 1.16 becomes 1160, 1.16.3 becomes 1163
     * @return The nms version formatted as a double. 4 digits (major[1]minor[2]patch[1]), i.e. 1_16_5 (1165) for v1_16_R3
     */
    public static int getFormattedNmsInteger() {
        if (formattedNms != -1) { return formattedNms; }
        formattedNms = NmsVersionParser.getFormattedNmsInteger(getMCVersion());
        return formattedNms;
    }

    private static Boolean isWineSpigot = null;
    public static boolean isWineSpigot() {
        if (isWineSpigot == null) {
            return isWineSpigot = Bukkit.getServer().getName().equals("WineSpigot");
        }
        return isWineSpigot;
    }
}
