package com.kamikazejam.kamicommon.menu.api.callbacks;

import com.kamikazejam.kamicommon.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A callback interface for handling {@link InventoryDragEvent}s in a {@link Menu}.<br>
 * This callback is invoked when a player drags items across slots in the menu inventory.
 */
@FunctionalInterface
public interface MenuDragCallback {
    /**
     * Called when a drag event occurs in the menu inventory.
     *
     * @param player The player who performed the drag
     * @param event The drag event
     */
    void onDrag(@NotNull Player player, @NotNull InventoryDragEvent event);
}
