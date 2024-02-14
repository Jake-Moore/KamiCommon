package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.nms.NmsManager;

public class StringUtilBukkit {

    private static Boolean supportsHexCodes = null;
    public static boolean supportsHexCodes() {
        if (supportsHexCodes == null) {
            try {
                // Will throw an exception on standalone instances or servers without bukkit (e.g. Velocity)
                Class.forName("org.bukkit.Bukkit");

                // IFF we have bukkit access, then we can use the NmsManager to check the version
                supportsHexCodes = NmsManager.getFormattedNmsDouble() >= 1160;

            } catch (ClassNotFoundException ignored) {
                supportsHexCodes = false;
            }
        }
        return supportsHexCodes;
    }

}
