package com.kamikazejam.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@SuppressWarnings("unused")
public interface MenuClickPlayer {
    void onPlayerClick(Player player, ClickType clickType);
}
