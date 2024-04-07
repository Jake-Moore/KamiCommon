package com.kamikazejam.kamicommon.jedis;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
@SuppressWarnings("unused")
public abstract class SpigotJedisHandler extends AbstractJedisHandler {
    private final Plugin plugin;

    public SpigotJedisHandler(Plugin plugin) {
        super((msg) -> plugin.getLogger().info(msg));
        this.plugin = plugin;
    }
    public SpigotJedisHandler(Plugin plugin, boolean debug) {
        super((msg) -> plugin.getLogger().info(msg), debug);
        this.plugin = plugin;
    }

    public abstract SpigotPubSubListener createSpigotPubSubListener();

    @Override
    public final PubSubListener createPubSubListener() {
        return createSpigotPubSubListener();
    }
}
