package com.kamikazejam.kamicommon.util.engine;

import com.kamikazejam.kamicommon.KamiCommon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class EngineTeleportMixinCause extends Engine {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final EngineTeleportMixinCause i = new EngineTeleportMixinCause();

    @Contract(pure = true)
    public static EngineTeleportMixinCause get() {
        return i;
    }

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    @Getter
    private boolean mixinCausedTeleportIncoming = false;

    public void setMixinCausedTeleportIncoming(boolean mixinCausedTeleportIncoming) {
        this.mixinCausedTeleportIncoming = mixinCausedTeleportIncoming;
    }

    private final Set<PlayerTeleportEvent> mixinCausedTeleportEvents = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // -------------------------------------------- //
    // TO BE USED
    // -------------------------------------------- //

    public boolean isCausedByTeleportMixin(PlayerTeleportEvent event) {
        return this.mixinCausedTeleportEvents.contains(event);
    }

    // -------------------------------------------- //
    // LISTENER
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOWEST)
    public void markEvent(final PlayerTeleportEvent event) {
        if (!mixinCausedTeleportIncoming) return;
        mixinCausedTeleportIncoming = false;
        mixinCausedTeleportEvents.add(event);
        Bukkit.getScheduler().scheduleSyncDelayedTask(KamiCommon.get(), () -> mixinCausedTeleportEvents.remove(event));
    }
}
