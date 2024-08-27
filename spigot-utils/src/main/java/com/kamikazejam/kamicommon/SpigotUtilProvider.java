package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.integrations.PlaceholderAPIIntegration;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.nms.provider.event.PreSpawnSpawnerAdapter;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SpigotUtilProvider {
    private static KamiPlugin plugin = null;
    public static @NotNull KamiPlugin getPlugin() {
        Preconditions.checkNotNull(plugin, "SpigotUtilPluginProvider was not initialized!!!");
        return plugin;
    }

    public static boolean isSet() {
        return plugin != null;
    }

    /**
     * @return true IFF the plugin was initialized, false if it was already initialized
     */
    public static boolean setPlugin(@NotNull KamiPlugin plugin) {
        // Only initialize once
        if (SpigotUtilProvider.plugin != null) { return false; }

        Preconditions.checkNotNull(plugin, "plugin");
        SpigotUtilProvider.plugin = plugin;

        // Setup Commands
        new KamiCommonCommandRegistration(plugin);
        // SetUp NMS Event Adapters
        plugin.registerListener(PreSpawnSpawnerAdapter.getSpawnerAdapter());

        return true;
    }

    @Setter private static @Nullable PremiumVanishIntegration vanishIntegration = null;
    private static Boolean vanishAPI = null;
    public static @Nullable PremiumVanishIntegration getVanishIntegration() {
        if (vanishAPI == null) {
            vanishAPI = Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish");
        }
        if (vanishAPI && vanishIntegration == null) {
            vanishIntegration = new PremiumVanishIntegration(getPlugin());
        }
        return vanishIntegration;
    }

    @Setter private static @Nullable PlaceholderAPIIntegration papiIntegration = null;
    private static Boolean papiAPI = null;
    public static @Nullable PlaceholderAPIIntegration getPlaceholderIntegration() {
        if (papiAPI == null) {
            papiAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        }
        if (papiAPI && papiIntegration == null) {
            papiIntegration = new PlaceholderAPIIntegration(getPlugin());
        }
        return papiIntegration;
    }
}
