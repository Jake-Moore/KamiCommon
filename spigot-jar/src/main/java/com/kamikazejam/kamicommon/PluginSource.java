package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.command.internal.KamiCommonCommand;
import com.kamikazejam.kamicommon.configuration.config.KamiConfig;
import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.engine.EngineTeleportMixinCause;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.mixin.*;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("unused")
public class PluginSource {
    private static @Nullable KamiPlugin pluginSource;
    private static boolean enabled = false;
    private static final KamiCommonCommand command = new KamiCommonCommand();

    /**
     * @return true IFF a plugin source was NEEDED and used for registration
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean onEnable(@NotNull KamiPlugin plugin) {
        if (pluginSource != null) { return false; }
        pluginSource = plugin;
        enabled = true;

        // Register all KamiCommon info needing a plugin
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);

        // Register Integrations
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            SpigotUtilProvider.setVanishIntegration(new PremiumVanishIntegration(plugin));
        }

        // Activate Actives
        EngineScheduledTeleport.get().setActive(plugin);
        EngineTeleportMixinCause.get().setActive(plugin);
        MixinPlayed.get().setActive(plugin);
        MixinDisplayName.get().setActive(plugin);
        MixinTeleport.get().setActive(plugin);
        MixinSenderPs.get().setActive(plugin);
        MixinWorld.get().setActive(plugin);

        // Schedule menu task to run every 1 tick
        Bukkit.getScheduler().runTaskTimer(plugin, new MenuTask(), 0L, 1L); // Every tick

        if (NmsVersion.isWineSpigot()) {
            info("WineSpigot (1.8.8) detected!");
        }

        // Create Yaml Loader
        info("Creating Yaml Loader");
        YamlUtil.getYaml();

        // Setup IdUtil
        IdUtilLocal.setup(plugin);

        // Provide SpigotUtils with this plugin as well
        SpigotUtilProvider.setPlugin(plugin);

        // Register the KamiCommon command
        command.registerCommand(plugin);
        return true;
    }

    /**
     * @return true IFF this call triggered the singleton disable sequence, false it already disabled
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean onDisable() {
        if (!enabled) { return false; }

        // Deactivate Actives
        EngineScheduledTeleport.get().setActive(null);
        EngineTeleportMixinCause.get().setActive(null);
        MixinPlayed.get().setActive(null);
        MixinDisplayName.get().setActive(null);
        MixinTeleport.get().setActive(null);
        MixinSenderPs.get().setActive(null);
        MixinWorld.get().setActive(null);

        // Unregister all listeners
        HandlerList.unregisterAll(get());

        // Save IdUtil
        IdUtilLocal.saveCachefileDatas();

        command.unregisterCommand();
        info("KamiCommon API disabled");

        boolean prev = enabled;
        enabled = false;
        return prev;
    }

    public static @NotNull JavaPlugin get() {
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

    // KamiConfig access of config.yml
    private static KamiConfig kamiConfig = null;
    public static @NotNull KamiConfig getKamiConfig() {
        final JavaPlugin plugin = get();
        if (kamiConfig == null) {
            kamiConfig = new KamiConfig(plugin, new File(plugin.getDataFolder(), "config.yml"), true, true);
        }
        return kamiConfig;
    }

    private static void registerWithPlugin(@NotNull JavaPlugin plugin) {


        enabled = true;
    }
}
