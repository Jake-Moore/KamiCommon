package com.kamikazejam.kamicommon.menu.api.icons.interfaces;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface UpdatingMenu {
    void updateOneTick();
    @NotNull Inventory getInventory();
}
