package com.kamikazejam.kamicommon.menu.api.clicks.transform;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface IClickTransform {
    void process(@NotNull InventoryClickEvent event, @NotNull Menu menu, @NotNull Player player, @NotNull MenuIcon icon, int slot);
}