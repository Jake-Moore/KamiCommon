package com.kamikazejam.kamicommon.nms.abstraction.event;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public interface EventManager {
    boolean verifyHandiness(@NotNull PlayerInteractEvent event);
    boolean verifyHandiness(@NotNull BlockPlaceEvent event);
}
