package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.command.impl.kc.KamiCommonCommand;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import com.kamikazejam.kamicommon.integrations.PlaceholderAPIIntegration;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.provider.event.PreSpawnSpawnerAdapter;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.engine.EngineTeleportMixinCause;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.mixin.*;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SpigotUtilsSource {
    private static KamiPlugin pluginSource = null;
    private static boolean enabled = false;
    private static final KamiCommonCommand kcCommand = new KamiCommonCommand();
    private static final MenuManager menuManager = new MenuManager();
    private static @Nullable BukkitTask menuTask = null;

    public static @NotNull KamiPlugin get() {
        Preconditions.checkNotNull(pluginSource, "SpigotUtilSource was not initialized!!!");
        return pluginSource;
    }

    public static boolean isSet() {
        return pluginSource != null;
    }

    /**
     * @return true IFF a plugin source was NEEDED and used for registration (false if already enabled)
     */
    public static boolean onEnable(@NotNull KamiPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");

        // Only initialize once
        if (SpigotUtilsSource.pluginSource != null) { return false; }
        SpigotUtilsSource.pluginSource = plugin;
        enabled = true;

        // Setup Commands
        new KamiCommonCommandRegistration(plugin);
        // SetUp NMS Event Adapters
        plugin.registerListeners(PreSpawnSpawnerAdapter.getSpawnerAdapter());
        // Register Core Command
        kcCommand.registerCommand(plugin); // Register the KamiCommon command

        // Register Menus
        plugin.getServer().getPluginManager().registerEvents(menuManager, plugin);
        menuTask = Bukkit.getScheduler().runTaskTimer(plugin, new MenuTask(), 0L, 1L); // Every tick

        // Setup IdUtil
        IdUtilLocal.setup(plugin);

        // Create Yaml Loader (since the first call to this is slow, force it during startup)
        YamlUtil.getYaml();

        // Activate Actives
        EngineScheduledTeleport.get().setActive(plugin);
        EngineTeleportMixinCause.get().setActive(plugin);
        MixinPlayed.get().setActive(plugin);
        MixinDisplayName.get().setActive(plugin);
        MixinTeleport.get().setActive(plugin);
        MixinSenderPs.get().setActive(plugin);
        MixinWorld.get().setActive(plugin);

        // Log detecting WineSpigot
        if (NmsVersion.isWineSpigot()) {
            info("WineSpigot (1.8.8) detected!");
        }

        return true;
    }

    /**
     * @return true IFF this call triggered the singleton disable sequence, false it already disabled
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean onDisable() {
        if (!enabled) { return false; }

        // Unregister KamiCommon Command
        kcCommand.unregisterCommand();

        // Save IdUtil
        IdUtilLocal.saveCachefileDatas();

        // Unregister Menu Listeners & Tasks
        HandlerList.unregisterAll(menuManager);
        if (menuTask != null) {
            Bukkit.getScheduler().cancelTask(menuTask.getTaskId());
            menuTask = null;
        }

        // Deactivate Actives
        EngineScheduledTeleport.get().setActive(null);
        EngineTeleportMixinCause.get().setActive(null);
        MixinPlayed.get().setActive(null);
        MixinDisplayName.get().setActive(null);
        MixinTeleport.get().setActive(null);
        MixinSenderPs.get().setActive(null);
        MixinWorld.get().setActive(null);

        boolean prev = enabled;
        enabled = false;
        return prev;
    }

    // Shortcut to KamiConfig, since we already have a KamiPlugin with one set up
    public static @NotNull KamiConfig getKamiConfig() {
        return get().getKamiConfig();
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

    @Setter private static @Nullable PremiumVanishIntegration vanishIntegration = null;
    private static Boolean vanishAPI = null;
    public static @Nullable PremiumVanishIntegration getVanishIntegration() {
        if (vanishAPI == null) {
            vanishAPI = Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish");
        }
        if (vanishAPI && vanishIntegration == null) {
            vanishIntegration = new PremiumVanishIntegration(get());
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
            papiIntegration = new PlaceholderAPIIntegration(get());
        }
        return papiIntegration;
    }
}
