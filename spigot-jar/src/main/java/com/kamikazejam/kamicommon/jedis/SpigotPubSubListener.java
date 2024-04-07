package com.kamikazejam.kamicommon.jedis;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public abstract class SpigotPubSubListener extends PubSubListener {
    private final Plugin plugin;

    public SpigotPubSubListener(Plugin plugin) {
        this.plugin = plugin;
    }
}
