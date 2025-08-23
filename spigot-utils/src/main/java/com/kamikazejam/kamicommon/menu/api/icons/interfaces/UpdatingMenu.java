package com.kamikazejam.kamicommon.menu.api.icons.interfaces;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface UpdatingMenu {
    @ApiStatus.Internal
    void updateOneTick();

    @NotNull Inventory getInventory();
}
