package com.kamikazejam.kamicommon.menu.api.callbacks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public interface MenuPostCloseCallback {
    void onPostClose(@NotNull Player player);
}
