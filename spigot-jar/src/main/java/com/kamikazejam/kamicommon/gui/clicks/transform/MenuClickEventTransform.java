package com.kamikazejam.kamicommon.gui.clicks.transform;

import com.kamikazejam.kamicommon.gui.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.xseries.XSound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class MenuClickEventTransform implements IClickTransform {

    private final @NotNull MenuClickEvent click;
    public MenuClickEventTransform(@NotNull MenuClickEvent click) {
        this.click = Objects.requireNonNull(click);
    }

    @Override
    public void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page) {
        player.playSound(player.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 2);
        click.onClick(player, event.getClick(), event);
    }
}