package com.kamikazejamplugins.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("unused")
public class MenuClickTransform implements MenuClickInfo {

    private final MenuClick click;
    public MenuClickTransform(MenuClick click) {
        this.click = click;
    }

    @Override
    public void onItemClickMember(Player player, InventoryClickEvent event) {
        if (click != null) {
            click.onItemClick(event.getClick());
        }
    }
}
