package com.kamikazejam.kamicommon.menu.api.clicks.transform.paginated;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Click Transform for {@link PaginatedMenu} menus.
 */
@SuppressWarnings("unused")
public interface IPaginatedClickTransform extends IClickTransform {

    /**
     * @param page The current page (0-indexed)
     */
    void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page);

    @Override
    default void process(@NotNull InventoryClickEvent event, @NotNull Menu menu, @NotNull Player player, @NotNull MenuIcon icon, int slot) {
        this.process(player, event, getPage(menu));
    }

    /**
     * @return The current page (0-indexed)
     */
    private int getPage(@NotNull Menu menu) {
        if (!(menu instanceof PaginatedMenu paginatedMenu)) { return 0; }
        return paginatedMenu.getCurrentPage();
    }
}