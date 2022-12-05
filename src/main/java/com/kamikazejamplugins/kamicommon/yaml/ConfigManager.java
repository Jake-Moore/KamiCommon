package com.kamikazejamplugins.kamicommon.yaml;

import com.kamikazejamplugins.kamicommon.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ConfigManager {

    /**
     * Returns a new configuration after creating and saving the file (does not load defaults)
     * @param plugin The JavaPlugin to grab the datafolder from
     * @param fileName The name of the file for the new configuration
     * @return The FileConfiguration which was created and saved to file
     */
    public static FileConfiguration createConfig(JavaPlugin plugin, String fileName) {
        return createConfig(plugin, plugin.getDataFolder(), fileName, false);
    }

    /**
     * Returns a new configuration after creating and saving the file (does not load defaults)
     * @param plugin The JavaPlugin for logging
     * @param dataFolder The datafolder to put the config file in
     * @param fileName The name of the file for the new configuration
     * @return The FileConfiguration which was created and saved to file
     */
    public static FileConfiguration createConfig(JavaPlugin plugin, File dataFolder, String fileName) {
        return createConfig(plugin, dataFolder, fileName, false);
    }

    /**
     * Returns a new configuration after creating and saving the file
     * @param plugin The JavaPlugin to grab the datafolder from
     * @param fileName The name of the file for the new configuration
     * @param defaultsFromResource Whether to load defaults from the jar resources folder
     * @return The FileConfiguration which was created and saved to file
     */
    public static FileConfiguration createConfig(JavaPlugin plugin, File dataFolder, String fileName, boolean defaultsFromResource) {
        //Initialize the rewards config
        File f = FileManager.createPluginFile(plugin, dataFolder.getPath(), fileName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        //Load defaults into the rewards config
        if (defaultsFromResource) {
            Reader defConfigStream2 = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8);
            YamlConfiguration defConfig2 = YamlConfiguration.loadConfiguration(defConfigStream2);
            config.addDefaults(defConfig2);
            config.options().copyDefaults(true);
        }

        //Save it before returning because why not
        saveConfig(plugin, dataFolder, config, fileName);
        return config;
    }

    /**
     * Saves a config to a file, returns only for chaining purposes
     * @param plugin The JavaPlugin to get the datafolder for
     * @param config The configuration to save to file
     * @param fileName The name of the file to save to
     */
    public static FileConfiguration saveConfig(JavaPlugin plugin, FileConfiguration config, String fileName) {
        File f = new File(plugin.getDataFolder(), fileName);
        try {
            config.save(f);
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + e.toString());
        }
        return config;
    }

    /**
     * Saves a config to a file, returns only for chaining purposes
     * @param plugin The JavaPlugin for logging
     * @param dataFolder The datafolder to save the file in
     * @param config The configuration to save to file
     * @param fileName The name of the file to save to
     */
    public static FileConfiguration saveConfig(JavaPlugin plugin, File dataFolder, FileConfiguration config, String fileName) {
        File f = new File(dataFolder, fileName);
        try {
            config.save(f);
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + e.toString());
        }
        return config;
    }

    /**
     * Reloads a config object from a file, make sure to set a variable for the returned configuration
     * @param plugin The JavaPlugin to get the datafolder for
     * @param fileName The name of the file to grab
     * @return The same config object for chaining
     */
    public static FileConfiguration reloadConfig(JavaPlugin plugin, String fileName) {
        File f = new File(plugin.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(f);
    }
}
