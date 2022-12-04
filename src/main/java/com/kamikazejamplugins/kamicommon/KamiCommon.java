package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.gui.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;

/*---
mvn install:install-file -Dfile="C:\Users\Jake\Desktop\Spigot Plugins\KamiCommon\target\KamiCommon-1.0.16.jar" -DgroupId=com.kamikazejamplugins -DartifactId=kamicommon -Dversion=1.0.16 -Dpackaging=jar
*/

@SuppressWarnings("unused")
public class KamiCommon {
    private static JavaPlugin plugin = null;

    public static void setupPlugin(JavaPlugin plugin) {
        if (KamiCommon.plugin == null) {
            KamiCommon.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        }
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static @Nullable File getDataFolder(JavaPlugin plugin) {
        File pluginFolder = plugin.getDataFolder().getParentFile();
        File kamicommon = new File(pluginFolder.getAbsolutePath() + File.separator + "KamiCommon");
        if (!kamicommon.exists()) {
            if (!kamicommon.mkdirs()) {
                plugin.getLogger().severe("Could not create a KamiCommon plugin folder");
                return null;
            }
        }
        return kamicommon;
    }

    public static void log(String s) {
        if (plugin != null) {
            plugin.getLogger().info(s);
        }else {
            System.out.println("[INFO] [KamiCommon] " + s);
        }
    }

    public static void warn(String s) {
        if (plugin != null) {
            plugin.getLogger().warning(s);
        }else {
            System.out.println("[WARN] [KamiCommon] " + s);
        }
    }

    public static void severe(String s) {
        if (plugin != null) {
            plugin.getLogger().severe(s);
        }else {
            System.err.println("[SEVERE] [KamiCommon] " + s);
        }
    }
}
