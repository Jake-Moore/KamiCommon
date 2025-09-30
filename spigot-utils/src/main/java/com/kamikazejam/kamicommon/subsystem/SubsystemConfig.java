package com.kamikazejam.kamicommon.subsystem;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

@SuppressWarnings("unused")
public abstract class SubsystemConfig<S extends AbstractSubsystem<?, S>> extends KamiConfigExt {
    private final @NotNull S subsystem;

    // Constructor for subsystems storing configs in Files on the server filesystem
    protected SubsystemConfig(
            @NotNull S subsystem,
            @NotNull String resourcePath
    ) {
        super(
                // Plugin
                subsystem.getPlugin(),
                // File on server filesystem
                subsystem.getConfigFileDestination(),
                // Supplier for config resource input stream
                () -> SubsystemConfig.getIS(subsystem, resourcePath)
        );
        this.subsystem = subsystem;

        // Add defaults to the config
        addConfigDefaults();
    }

    /**
     * Constructor for subsystems using ConfigSource (e.g. from a database or other source)
     * @param subsystem The parent subsystem
     * @param source The config source where the yaml contents will be read from
     * @param resourcePath The resource path to the default config file inside the plugin jar
     */
    protected SubsystemConfig(
            @NotNull S subsystem,
            @NotNull ConfigSource source,
            @NotNull String resourcePath
    ) {
        super(
                // Plugin
                subsystem.getPlugin(),
                // Config source
                source,
                // Supplier for config resource input stream
                () -> SubsystemConfig.getIS(subsystem, resourcePath)
        );
        this.subsystem = subsystem;

        // Add defaults to the config
        addConfigDefaults();
    }

    /**
     * Helper method to get a resource {@link InputStream} from a subsystem's plugin.
     */
    public static @NotNull InputStream getIS(@NotNull AbstractSubsystem<?,?> subsystem, @NotNull String resourcePath) {
        return Preconditions.checkNotNull(
                subsystem.getPlugin().getResource(resourcePath),
                "Subsystem ('" + subsystem.getName() + "') resource stream is null: '" + resourcePath + "'"
        );
    }

    @Internal
    public abstract void addConfigDefaults();

    @NotNull
    public S getSubsystem() {
        return subsystem;
    }

    /**
     * Reads a {@link Location} from the current config file using the standard location format.<br>
     *
     * @param key The root key of the location in the config file, e.g. "spawn" which has subkeys: "spawn.x", "spawn.y", etc.
     */
    @NotNull
    public Location getLocation(String key) throws IllegalArgumentException {
        World world = Bukkit.getWorld(getString(key + ".world"));
        if (world == null) {
            String err = "[" + subsystem.getName() + "Config] Invalid world for location: " + key;
            throw new IllegalArgumentException(err);
        }

        double x = getDouble(key + ".x");
        double y = getDouble(key + ".y");
        double z = getDouble(key + ".z");
        float yaw = getFloat(key + ".yaw");
        float pitch = getFloat(key + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
