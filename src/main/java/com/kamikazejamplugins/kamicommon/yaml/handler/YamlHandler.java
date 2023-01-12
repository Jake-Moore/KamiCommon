package com.kamikazejamplugins.kamicommon.yaml.handler;

import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public class YamlHandler extends AbstractYamlHandler {
    @Nonnull private final JavaPlugin plugin;

    public YamlHandler(@Nonnull JavaPlugin plugin, File configFile) {
        super(configFile);
        this.plugin = plugin;
    }

    public YamlHandler(@Nonnull JavaPlugin plugin, File configFile, String fileName) {
        super(configFile, fileName);
        this.plugin = plugin;
    }

    @Override
    public InputStream getIS() {
        return plugin.getResource(configFile.getName());
    }

    @Override
    public void error(String s) {
        plugin.getLogger().severe(s);
    }
}
