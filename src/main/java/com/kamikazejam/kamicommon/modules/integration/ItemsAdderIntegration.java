package com.kamikazejam.kamicommon.modules.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class ItemsAdderIntegration extends ModuleIntegration {
    public ItemsAdderIntegration(KamiPlugin plugin) {
        super(plugin);

        // Call onItemsAdderLoaded if the plugin has already loaded (i.e. on reload or if depended)
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            getPlugin().getModuleManager().onItemsAdderLoaded();
        }
    }

    @EventHandler
    public void onItemsAdder(ItemsAdderLoadDataEvent event) {
        getPlugin().getLogger().info("[ItemsAdderLoadDataEvent] Calling Modules...");
        getPlugin().getModuleManager().onItemsAdderLoaded();
    }
}
