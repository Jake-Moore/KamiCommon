package com.kamikazejam.kamicommon.gui.interfaces;

import com.kamikazejam.kamicommon.xseries.XSound;
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
            player.playSound(player.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 2);
            click.onItemClickMember(player, event.getClick());
        }
    }
}
