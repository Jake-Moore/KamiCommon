package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringUtilBukkit {

    private static Boolean supportsHexCodes = null;
    public static boolean supportsHexCodes() {
        if (supportsHexCodes == null) {
            try {
                // Will throw an exception on standalone instances or servers without bukkit (e.g. Velocity)
                Class.forName("org.bukkit.Bukkit");

                // IFF we have bukkit access, then we can use the NmsManager to check the version
                String nms = getNMSVersion();
                supportsHexCodes = NmsVersionParser.getFormattedNmsDouble(nms) >= 1160;

            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
                supportsHexCodes = false;
            }
        }
        return supportsHexCodes;
    }


    private static String nmsVersion = null;
    /**
     * Normalizes the package name to the format used by the NMS classes.
     * @return The nms version, Ex: "v1_8_R3" or "v1_19_R2"
     */
    private static String getNMSVersion() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (nmsVersion != null) { return nmsVersion; }

        Class<?> serverClass = Class.forName("org.bukkit.Bukkit");
        Method getServerMethod = serverClass.getDeclaredMethod("getServer");
        Object serverObject = getServerMethod.invoke(null);
        String v = serverObject.getClass().getPackage().getName();
        nmsVersion = NmsVersionParser.normalizePackage(v.substring(v.lastIndexOf('.') + 1));
        return nmsVersion;
    }
}
