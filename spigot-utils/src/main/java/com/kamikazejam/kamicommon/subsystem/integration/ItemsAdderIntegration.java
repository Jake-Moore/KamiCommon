package com.kamikazejam.kamicommon.subsystem.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;

public class ItemsAdderIntegration extends SubsystemIntegration {
    public ItemsAdderIntegration(KamiPlugin plugin) {
        super(plugin);
    }

    // This is delayed from boot, and should always be called on initial startup
    @EventHandler
    public void onItemsAdder(ItemsAdderLoadDataEvent event) {
        getPlugin().getLogger().info("[ItemsAdderLoadDataEvent] (" + event.getCause() + ") Calling Subsystems...");
        getPlugin().getModuleManager().onItemsAdderLoaded();
        getPlugin().getFeatureManager().onItemsAdderLoaded();
    }
}
