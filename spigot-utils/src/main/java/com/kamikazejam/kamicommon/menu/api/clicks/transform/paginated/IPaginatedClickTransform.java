package com.kamikazejam.kamicommon.menu.api.clicks.transform.paginated;

import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.paginated.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Click Transform for {@link PaginatedMenu} menus.
 */
@SuppressWarnings("unused")
public interface IPaginatedClickTransform extends IClickTransform {

    void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page);

}