package com.kamikazejam.kamicommon.nms.event;

import com.kamikazejam.kamicommon.nms.abstraction.event.EventManager;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class EventManager_1_8_R1 implements EventManager {
    @Override
    public boolean verifyHandiness(@NotNull PlayerInteractEvent event) {
        return event.getAction() != Action.PHYSICAL;
    }

    @Override
    public boolean verifyHandiness(@NotNull BlockPlaceEvent event) {
        return true;
    }
}
