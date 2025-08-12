package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public class YamlHandler extends AbstractYamlHandler<YamlConfiguration> {
    @NotNull private final JavaPlugin plugin;

    public YamlHandler(AbstractConfig<?> abstractConfig, @NotNull JavaPlugin plugin, File configFile) {
        super(abstractConfig, configFile);
        this.plugin = plugin;
    }

    public YamlHandler(AbstractConfig<?> abstractConfig, @NotNull JavaPlugin plugin, File configFile, String fileName) {
        super(abstractConfig, configFile, fileName);
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

    @Override
    public YamlConfiguration newConfig(MappingNode node, File configFile) {
        return new YamlConfiguration(node, configFile);
    }

    @Override
    public MemorySectionMethods<?> newMemorySection(MappingNode node) {
        return new MemorySection(node, "", null);
    }
}
