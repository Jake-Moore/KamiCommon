package com.kamikazejam.kamicommon.menu.clicks.transform.simple;

import com.kamikazejam.kamicommon.menu.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.clicks.transform.IClickTransform;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class SimpleMenuClickEventTransform implements ISimpleClickTransform {

    private final @NotNull MenuClickEvent click;
    public SimpleMenuClickEventTransform(@NotNull MenuClickEvent click) {
        this.click = Objects.requireNonNull(click);
    }

    @Override
    public void process(@NotNull Player player, @NotNull InventoryClickEvent event) {
        click.onClick(player, event.getClick(), event);
    }
}
