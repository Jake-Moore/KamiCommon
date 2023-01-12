package com.kamikazejamplugins.kamicommon;

import org.bukkit.plugin.java.JavaPlugin;

/*---
mvn install:install-file -Dfile="C:\Users\Jake\Desktop\Spigot Plugins\KamiCommon\target\KamiCommon-1.0.16.jar" -DgroupId=com.kamikazejamplugins -DartifactId=kamicommon -Dversion=1.0.16 -Dpackaging=jar
*/

@SuppressWarnings("unused")
public class KamiCommon extends JavaPlugin {
    private static KamiCommon plugin;

    @Override
    public void onEnable() {
        plugin = this;
    }

    @Override
    public void onDisable() {

    }

    public static JavaPlugin get() {
        return plugin;
    }
}
