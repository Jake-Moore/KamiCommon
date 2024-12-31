package com.kamikazejam.kamicommon.menu.callbacks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface MenuOpenCallback {
    void onOpen(@NotNull Player player, @NotNull InventoryView view);
}
