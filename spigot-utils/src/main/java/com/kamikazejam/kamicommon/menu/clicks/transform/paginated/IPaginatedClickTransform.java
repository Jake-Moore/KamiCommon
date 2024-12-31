package com.kamikazejam.kamicommon.menu.clicks.transform.paginated;

import com.kamikazejam.kamicommon.menu.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.clicks.transform.simple.ISimpleClickTransform;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Click Transform for {@link com.kamikazejam.kamicommon.menu.PaginatedMenu} menus.
 */
@SuppressWarnings("unused")
public interface IPaginatedClickTransform extends IClickTransform {

    void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page);

}