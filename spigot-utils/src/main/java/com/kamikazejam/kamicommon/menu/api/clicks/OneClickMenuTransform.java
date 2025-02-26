package com.kamikazejam.kamicommon.menu.api.clicks;

import com.kamikazejam.kamicommon.menu.OneClickMenu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface OneClickMenuTransform {
    void onClick(@NotNull OneClickMenu menu, @NotNull InventoryClickEvent event, @NotNull Player player, @NotNull MenuIcon icon, int slot);
}
