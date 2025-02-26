package com.kamikazejam.kamicommon.menu.api.clicks.transform.simple;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.SimpleMenu;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Click Transform for {@link SimpleMenu} menus.
 */
@SuppressWarnings("unused")
public interface ISimpleClickTransform extends IClickTransform {

    void process(@NotNull Player player, @NotNull InventoryClickEvent event);

    @Override
    default void process(@NotNull InventoryClickEvent event, @NotNull Menu menu, @NotNull Player player, @NotNull MenuIcon icon, int slot) {
        this.process(player, event);
    }
}