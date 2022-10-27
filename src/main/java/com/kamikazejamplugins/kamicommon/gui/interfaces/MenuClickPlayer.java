package com.kamikazejamplugins.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface MenuClickPlayer<T extends Player> {

    void onItemClickMember(T member, ClickType clickType);
}
