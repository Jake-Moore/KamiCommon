package com.kamikazejam.kamicommon.configuration.config;

import com.kamikazejam.kamicommon.yaml.spigot.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link KamiConfig} but with some extended features
 */
@SuppressWarnings("unused")
public class KamiConfigExt extends KamiConfig {
    public KamiConfigExt(@NotNull JavaPlugin plugin, File file) {
        super(plugin, file);
    }
    public KamiConfigExt(@NotNull JavaPlugin plugin, File file, boolean addDefaults) {
        super(plugin, file, addDefaults);
    }
    public KamiConfigExt(@NotNull JavaPlugin plugin, File file, boolean addDefaults, boolean strictKeys) {
        super(plugin, file, addDefaults);
        this.setStrictKeys(strictKeys);
    }
    public KamiConfigExt(@NotNull JavaPlugin plugin, File file, Supplier<InputStream> defaultSupplier) {
        super(plugin, file, defaultSupplier);
    }

    @Override
    public String getString(String key) {
        return this.getString(key, null);
    }

    @Override
    public String getString(String key, String def) {
        return this.applyThisPlaceholders(super.getString(key, def));
    }

    @Override
    public List<String> getStringList(String key) {
        return this.getStringList(key, null);
    }

    @Override
    public List<String> getStringList(String key, List<String> def) {
        List<String> list = super.getStringList(key, def);
        list.replaceAll(this::applyThisPlaceholders);
        return list;
    }

    public @NotNull String applyThisPlaceholders(@NotNull String val) {
        if (!this.isConfigurationSection("this.placeholders")) { return val; }

        MemorySection section = this.getConfigurationSection("this.placeholders");
        if (section == null) { return val; }

        // Recursion base case, if there are no placeholders just return the string
        if (!val.contains("{") || !val.contains("}")) { return val; }

        for (String placeholder : section.getKeys(false)) {
            // This is a tradeoff, we can reduce the amount of recursions when fetching placeholders that contain
            //  other placeholders, at the cost of another contains call (worth it imo)
            if (!val.contains("{" + placeholder + "}")) { continue; }

            // Recursion base case, if there are no placeholders just return the string
            val = val.replace("{" + placeholder + "}", section.getString(placeholder));
        }
        return val;
    }
}
