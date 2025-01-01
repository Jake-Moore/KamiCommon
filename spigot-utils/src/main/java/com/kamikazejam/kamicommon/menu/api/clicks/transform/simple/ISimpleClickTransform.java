package com.kamikazejam.kamicommon.menu.api.clicks.transform.simple;

import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.simple.SimpleMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Click Transform for {@link SimpleMenu} menus.
 */
@SuppressWarnings("unused")
public interface ISimpleClickTransform extends IClickTransform {

    void process(@NotNull Player player, @NotNull InventoryClickEvent event);

}