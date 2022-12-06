package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.config.testing.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Config config = new Config(this, new File(getDataFolder(), "config.yml"));
        config.save();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
