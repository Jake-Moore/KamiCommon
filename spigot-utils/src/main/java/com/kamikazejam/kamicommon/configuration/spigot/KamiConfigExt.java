package com.kamikazejam.kamicommon.configuration.spigot;

import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import com.kamikazejam.kamicommon.yaml.spigot.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link KamiConfig} but with some extended features
 */
@SuppressWarnings("unused")
public class KamiConfigExt extends KamiConfig {

    // -------------------------------------------------- //
    //               JavaPlugin Constructors              //
    // -------------------------------------------------- //

    /**
     * Creates a new config instance with the given plugin and destination file.<br><br>
     * This constructor enables defaults using the following resource file method:<br>
     * - Assumes a resource file with the same name as the provided file, exists in the current jar.
     */
    public KamiConfigExt(@NotNull JavaPlugin plugin, @NotNull File file) {
        super(plugin, file);
    }

    /**
     * Creates a new config instance with the given plugin and destination file.<br><br>
     * This constructor uses defaults if and only if the provided supplier is NOT null:<br>
     * - Providing a non-null supplier will enable defaults using the provided InputStream
     * - Providing a null supplier will disable defaults
     *
     * @param defaultsStream The optional supplier to load defaults from.
     */
    public KamiConfigExt(@NotNull JavaPlugin plugin, @NotNull File file, @Nullable Supplier<InputStream> defaultsStream) {
        super(plugin, file, defaultsStream);
    }

    /**
     * Creates a new config instance with the given plugin and config source.<br><br>
     * This constructor enables defaults using the following resource file method:<br>
     * - Fetches the resource file. Its path is determined by {@link ConfigSource#getResourceStreamPath()}
     *
     * @param source The source to load and save the config from.
     */
    public KamiConfigExt(@NotNull JavaPlugin plugin, @NotNull ConfigSource source) {
        super(plugin, source);
    }

    /**
     * Creates a new config instance with the given plugin and config source.<br><br>
     * This constructor uses defaults if and only if the provided supplier is NOT null:<br>
     * - Providing a non-null supplier will enable defaults using the provided InputStream
     * - Providing a null supplier will disable defaults
     *
     * @param source The source to load and save the config from.
     * @param defaultsStream The optional supplier to load defaults from.
     */
    public KamiConfigExt(@NotNull JavaPlugin plugin, @NotNull ConfigSource source, @Nullable Supplier<InputStream> defaultsStream) {
        super(plugin, source, defaultsStream);
    }

    // -------------------------------------------------- //
    //                Subsystem Constructors              //
    // -------------------------------------------------- //

    /**
     * Creates a new config instance with the given subsystem and destination file.<br><br>
     * This constructor enables defaults using the following resource file method:<br>
     * - Fetches the resource file using the provided file name, from {@link AbstractSubsystem#getSupplementalConfigResource(File)}
     */
    public KamiConfigExt(@NotNull AbstractSubsystem<?, ?> subsystem, @NotNull File file) {
        super(subsystem, file);
    }

    /**
     * Creates a new config instance with the given subsystem and destination file.<br><br>
     * This constructor uses defaults if and only if the provided supplier is NOT null:<br>
     * - Providing a non-null supplier will enable defaults using the provided InputStream
     * - Providing a null supplier will disable defaults
     */
    public KamiConfigExt(@NotNull AbstractSubsystem<?, ?> subsystem, @NotNull File file, @Nullable Supplier<InputStream> defaultsStream) {
        super(subsystem, file, defaultsStream);
    }

    /**
     * Creates a new config instance with the given subsystem and config source.<br><br>
     * This constructor enables defaults using the following resource file method:<br>
     * - Fetches the resource file using the provided source, from {@link AbstractSubsystem#getSupplementalConfigResource(ConfigSource)}
     *
     * @param source The source to load and save the config from.
     */
    public KamiConfigExt(@NotNull AbstractSubsystem<?, ?> subsystem, @NotNull ConfigSource source) {
        super(subsystem, source);
    }

    /**
     * Creates a new config instance with the given subsystem and config source.<br><br>
     * This constructor uses defaults if and only if the provided supplier is NOT null:<br>
     * - Providing a non-null supplier will enable defaults using the provided InputStream
     * - Providing a null supplier will disable defaults
     *
     * @param source The source to load and save the config from.
     * @param defaultsStream The optional supplier to load defaults from.
     */
    public KamiConfigExt(@NotNull AbstractSubsystem<?, ?> subsystem, @NotNull ConfigSource source, @Nullable Supplier<InputStream> defaultsStream) {
        super(subsystem, source, defaultsStream);
    }

    @Override
    public String getString(String key) {
        return this.getString(key, null);
    }

    @Override
    public String getString(String key, String def) {
        String string = super.getString(key, def);
        if (string == null) { return null; }
        return this.applyThisPlaceholders(string);
    }

    @Override
    public List<String> getStringList(String key) {
        return this.getStringList(key, null);
    }

    @Override
    public List<String> getStringList(String key, List<String> def) {
        List<String> list = super.getStringList(key, def);
        if (list == null) { return null; }
        list.replaceAll(this::applyThisPlaceholders);
        return list;
    }

    @SuppressWarnings("DuplicatedCode")
    public String applyThisPlaceholders(String val) {
        if (val == null) { return null; }
        if (!this.isConfigurationSection("this.placeholders")) { return val; }

        MemorySection section = this.getConfigurationSection("this.placeholders");

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
