package com.kamikazejam.kamicommon.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("unused")
public class MenuClickPlayerTransform implements MenuClickInfo {

    private final MenuClickPlayer click;
    public MenuClickPlayerTransform(MenuClickPlayer click) {
        this.click = click;
    }

    @Override
    public void onItemClickMember(Player player, InventoryClickEvent event) {
        if (click != null) {
            click.onItemClickMember(player, event.getClick());
        }
    }
}
