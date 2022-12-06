package com.kamikazejamplugins.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@SuppressWarnings("unused")
public interface MenuClickPlayer<T extends Player> {

    void onItemClickMember(T member, ClickType clickType);
}
