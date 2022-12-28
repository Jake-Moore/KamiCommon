package com.kamikazejamplugins.kamicommon.yaml.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;

public class PluginIS {
    public static InputStream getIS(@Nullable Object plugin, File configFile) {
        if (plugin instanceof JavaPlugin) {
            JavaPlugin javaPlugin = (JavaPlugin) plugin;
            return javaPlugin.getResource(configFile.getName());
        }else {
            return PluginIS.class.getClassLoader().getResourceAsStream(File.separator + configFile.getName());
        }
    }
}
