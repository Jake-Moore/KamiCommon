package com.kamikazejam.kamicommon.menu.api.callbacks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface MenuCloseCallback {
    void onClose(@NotNull Player player, @NotNull InventoryCloseEvent event);
}
