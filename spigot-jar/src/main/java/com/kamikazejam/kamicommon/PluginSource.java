package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class exists as a bootloader for the {@link KamiCommon} plugin & APIs.<br>
 * As of v4 of the KamiCommon library, most of the APIs were moved to the spigot-utils module, and this class
 * has less responsibility, mainly to initialize the {@link SpigotUtilsSource} from spigot-utils.
 */
@SuppressWarnings("unused")
public class PluginSource {
    private static @Nullable KamiPlugin pluginSource;
    private static boolean enabled = false;

    /**
     * @return true IFF a plugin source was NEEDED and used for registration (false if already enabled)
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean onEnable(@NotNull KamiPlugin plugin) {
        if (enabled) { return false; }
        pluginSource = plugin;
        enabled = true;

        // Initialize SpigotUtils with this plugin as well
        SpigotUtilsSource.onEnable(plugin);

        return true;
    }

    /**
     * @return true IFF this call triggered the singleton disable sequence, false it already disabled
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean onDisable() {
        if (!enabled) { return false; }

        // LAST THING: Disable SpigotUtils
        SpigotUtilsSource.onDisable();

        boolean prev = enabled;
        enabled = false;
        return prev;
    }

    public static @NotNull KamiPlugin get() {
        if (pluginSource == null) {
            throw new RuntimeException("Plugin source not set");
        }
        return pluginSource;
    }

    public static void info(@NotNull String msg) {
        if (pluginSource == null) {
            System.out.println("[INFO] " + msg);
        }else {
            pluginSource.getLogger().info(msg);
        }
    }
    public static void warning(@NotNull String msg) {
        if (pluginSource == null) {
            System.out.println("[WARNING] " + msg);
        }else {
            pluginSource.getLogger().warning(msg);
        }
    }
    public static void error(@NotNull String msg) {
        if (pluginSource == null) {
            System.out.println("[ERROR] " + msg);
        }else {
            pluginSource.getLogger().severe(msg);
        }
    }

    // Shortcut to KamiConfig, since we already have a KamiPlugin with one set up
    public static @NotNull KamiConfig getKamiConfig() {
        return get().getKamiConfig();
    }
}
