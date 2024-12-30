package com.kamikazejam.kamicommon.modules.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import lombok.Getter;
import org.bukkit.event.Listener;

@Getter
public abstract class ModuleIntegration implements Listener {
    private final KamiPlugin plugin;
    public ModuleIntegration(KamiPlugin plugin) {
        this.plugin = plugin;
        this.plugin.registerListeners(this);
    }
}
