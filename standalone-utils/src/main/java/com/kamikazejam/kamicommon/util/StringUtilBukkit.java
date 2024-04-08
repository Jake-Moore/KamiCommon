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
                String mcVer = getMCVersion();
                supportsHexCodes = NmsVersionParser.getFormattedNmsDouble(mcVer) >= 1160;

            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
                supportsHexCodes = false;
            }
        }
        return supportsHexCodes;
    }


    private static String mcVersion = null;
    /**
     * Returns the MC version of the server (i.e. 1.8.8 or 1.20.4) - via reflection
     * @return The MC version, Ex: "1.8.8" or "1.20.4"
     */
    private static String getMCVersion() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (mcVersion != null) { return mcVersion; }

        Class<?> serverClass = Class.forName("org.bukkit.Bukkit");
        Method getServerMethod = serverClass.getDeclaredMethod("getServer");
        Object serverObject = getServerMethod.invoke(null);
        Method getBukkitVersionMethod = serverObject.getClass().getDeclaredMethod("getBukkitVersion");
        String bukkitVer = (String) getBukkitVersionMethod.invoke(serverObject);
        mcVersion = bukkitVer.split("-")[0]; // i.e. 1.20.4 or 1.8.8
        return mcVersion;
    }
}
