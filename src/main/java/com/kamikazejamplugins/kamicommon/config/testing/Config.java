package com.kamikazejamplugins.kamicommon.config.testing;

import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config extends KamiConfig {
    public Config(JavaPlugin plugin, File file) {
        super(plugin, file, false);
    }

    public static void main(String[] args) {
        Config config = new Config(null, new File("C:\\Users\\Jake\\Desktop\\config2.yml"));
        //config.addDefault("test", "test» yay");
        config.save();
        config.reload();
    }
}