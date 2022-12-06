package com.kamikazejamplugins.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("unused")
public class MenuClickPlayerTransform<T extends Player> implements MenuClickInfo<T> {

    private final MenuClickPlayer<T> click;
    public MenuClickPlayerTransform(MenuClickPlayer<T> click) {
        this.click = click;
    }

    @Override
    public void onItemClickMember(T member, InventoryClickEvent event) {
        if (click != null) {
            click.onItemClickMember(member, event.getClick());
        }
    }

}
