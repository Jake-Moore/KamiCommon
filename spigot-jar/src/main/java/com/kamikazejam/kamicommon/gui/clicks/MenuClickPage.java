package com.kamikazejam.kamicommon.gui.clicks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface MenuClickPage {
    void onClick(@NotNull Player player, @NotNull ClickType clickType, int page);
}
