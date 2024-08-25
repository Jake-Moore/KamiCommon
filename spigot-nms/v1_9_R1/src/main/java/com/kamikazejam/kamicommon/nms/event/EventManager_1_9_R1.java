package com.kamikazejam.kamicommon.nms.event;

import com.kamikazejam.kamicommon.nms.abstraction.event.EventManager;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class EventManager_1_9_R1 implements EventManager {
    @Override
    public boolean verifyHandiness(@NotNull PlayerInteractEvent event) {
        return event.getHand() == null || event.getHand() == EquipmentSlot.HAND;
    }

    @Override
    public boolean verifyHandiness(@NotNull BlockPlaceEvent event) {
        return event.getHand() == EquipmentSlot.HAND;
    }
}
