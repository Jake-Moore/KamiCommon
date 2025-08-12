package com.kamikazejam.kamicommon.subsystem.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import lombok.Getter;
import org.bukkit.event.Listener;

@Getter
public abstract class SubsystemIntegration implements Listener {
    private final KamiPlugin plugin;
    public SubsystemIntegration(KamiPlugin plugin) {
        this.plugin = plugin;
        this.plugin.registerListeners(this);
    }
}
