package com.kamikazejam.kamicommon.modules.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class MythicMobsIntegration extends ModuleIntegration {
    public MythicMobsIntegration(KamiPlugin plugin) {
        super(plugin);

        // Call onMythicMobsLoaded if the plugin has already loaded (i.e. on reload or if depended)
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            getPlugin().getLogger().info("[MythicReloadedEvent] (STARTUP) Calling Modules ...");
            getPlugin().getModuleManager().onMythicMobsLoaded();
        }
    }

    @EventHandler
    public void onMythicMobs(MythicReloadedEvent event) {
        getPlugin().getLogger().info("[MythicReloadedEvent] Calling Modules in 1 second ...");

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                getPlugin().getModuleManager().onMythicMobsLoaded(), 20L);
    }
}
