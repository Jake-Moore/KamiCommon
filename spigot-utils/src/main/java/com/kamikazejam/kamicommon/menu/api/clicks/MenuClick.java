package com.kamikazejam.kamicommon.menu.api.clicks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface MenuClick {
    void onClick(@NotNull Player player, @NotNull ClickType clickType);
}