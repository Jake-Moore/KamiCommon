package com.kamikazejamplugins.kamicommon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class FileManager {
    //Creates an empty plugin file at a specified path with a specified name
    public static File createPluginFile(JavaPlugin plugin, String folder, String fileName) {
        File file = new File(folder, fileName);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    plugin.getLogger().info("&aCreated plugin file: " + fileName);
                }else {
                    plugin.getLogger().warning("Could not create plugin file: " + fileName);
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
            } catch (IOException e) {
                plugin.getLogger().info("&cCould not create plugin file: " + fileName);
            }
        }

        return file;
    }
}
