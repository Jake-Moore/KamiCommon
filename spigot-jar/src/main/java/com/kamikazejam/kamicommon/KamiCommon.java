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
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Getter
@SuppressWarnings("unused")
public class KamiCommon extends KamiPlugin implements Listener {
    private static KamiCommon plugin;
    private final KamiCommonCommand command = new KamiCommonCommand();

    @Override
    public void onEnableInner(){
        getLogger().info("KamiCommon enabling...");

        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        getServer().getPluginManager().registerEvents(this, this);

        // Register Integrations
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            SpigotUtilProvider.setVanishIntegration(new PremiumVanishIntegration(this));
        }

        // Activate Actives
        EngineScheduledTeleport.get().setActive(this);
        EngineTeleportMixinCause.get().setActive(this);
        MixinPlayed.get().setActive(this);
        MixinDisplayName.get().setActive(this);
        MixinTeleport.get().setActive(this);
        MixinSenderPs.get().setActive(this);
        MixinWorld.get().setActive(this);

        // Schedule menu task to run every 1 tick
        Bukkit.getScheduler().runTaskTimer(this, new MenuTask(), 0L, 1L); // Every tick

        if (isWineSpigot()) {
            getLogger().info("WineSpigot (1.8.8) detected!");
        }

        // Create Yaml Loader
        getLogger().info("Creating Yaml Loader");
        YamlUtil.getYaml();

        // Setup IdUtil
        IdUtilLocal.setup(this);

        SpigotUtilProvider.setPlugin(this);
        command.registerCommand(this);
    }

    @Override
    public void onDisableInner() {
        // Deactivate Actives
        EngineScheduledTeleport.get().setActive(null);
        EngineTeleportMixinCause.get().setActive(null);
        MixinPlayed.get().setActive(null);
        MixinDisplayName.get().setActive(null);
        MixinTeleport.get().setActive(null);
        MixinSenderPs.get().setActive(null);
        MixinWorld.get().setActive(null);

        // Unregister all listeners
        HandlerList.unregisterAll((Plugin) plugin);

        // Save IdUtil
        IdUtilLocal.saveCachefileDatas();

        command.unregisterCommand();
        Bukkit.getLogger().info("KamiCommon disabled");
    }

    public static KamiCommon get() {
        return plugin;
    }


    public static boolean isWineSpigot() {
        return NmsVersion.isWineSpigot();
    }

    private KamiConfig kamiConfig = null;
    public @NotNull KamiConfig getKamiConfig() {
        if (kamiConfig == null) {
            kamiConfig = new KamiConfig(this, new File(getDataFolder(), "config.yml"), true, true);
        }
        return kamiConfig;
    }
}
