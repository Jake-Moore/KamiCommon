package com.kamikazejam.kamicommon.subsystem.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class CitizensIntegration extends SubsystemIntegration {
    public CitizensIntegration(KamiPlugin plugin) {
        super(plugin);

        // Call onCitizensLoaded if the plugin has already loaded (i.e. on reload or if depended on)
        //   Requires Citizens NPC registry to be loaded, so it's safe even if this code runs before Citizens
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null && CitizensAPI.getNPCRegistry() != null) {
            getPlugin().getModuleManager().onCitizensLoaded();
            getPlugin().getFeatureManager().onCitizensLoaded();
        }
    }

    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        getPlugin().getLogger().info("[CitizensEnableEvent] Calling Subsystems in 1 second ...");
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            getPlugin().getModuleManager().onCitizensLoaded();
            getPlugin().getFeatureManager().onCitizensLoaded();
        }, 20L);
    }
    @EventHandler
    public void onCitizensLoaded(CitizensReloadEvent event) {
        getPlugin().getLogger().info("[CitizensReloadEvent] Calling Subsystems in 1 second ...");
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            getPlugin().getModuleManager().onCitizensLoaded();
            getPlugin().getFeatureManager().onCitizensLoaded();
        }, 20L);
    }
}
