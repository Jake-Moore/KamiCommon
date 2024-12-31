package com.kamikazejam.kamicommon.menu;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

interface UpdatingMenu {
    void updateOneTick();
    @NotNull Inventory getInventory();
}
