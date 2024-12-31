package com.kamikazejam.kamicommon.menu.clicks.transform.simple;

import com.kamikazejam.kamicommon.menu.clicks.transform.IClickTransform;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Click Transform for {@link com.kamikazejam.kamicommon.menu.SimpleMenu} menus.
 */
@SuppressWarnings("unused")
public interface ISimpleClickTransform extends IClickTransform {

    void process(@NotNull Player player, @NotNull InventoryClickEvent event);

}