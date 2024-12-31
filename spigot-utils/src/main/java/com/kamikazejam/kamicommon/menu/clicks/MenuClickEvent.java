package com.kamikazejam.kamicommon.menu.clicks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface MenuClickEvent {
    void onClick(@NotNull Player player, @NotNull ClickType clickType, @NotNull InventoryClickEvent event);
}
