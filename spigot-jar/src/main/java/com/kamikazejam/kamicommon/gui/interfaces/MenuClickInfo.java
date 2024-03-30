package com.kamikazejam.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("unused")
public interface MenuClickInfo {

    void onItemClickMember(Player player, InventoryClickEvent event);

}