package com.kamikazejamplugins.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("unused")
public interface MenuClickInfo<T extends Player> {

    void onItemClickMember(T member, InventoryClickEvent event);

}