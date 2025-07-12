package com.kamikazejam.kamicommon.menu.api.clicks.data;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Data class to hold information about a click event on a {@link Menu}.
 */
@Getter
public class MenuClickData<M extends Menu<M>> {
    /**
     * The {@link Menu} that was clicked.
     */
    private final @NotNull M menu;

    /**
     * The {@link Player} who clicked the menu.
     */
    private final @NotNull Player player;

    /**
     * The {@link ClickType} of the click event.
     */
    private final @NotNull ClickType clickType;

    /**
     * The raw {@link InventoryClickEvent} event from the click.
     */
    private final @NotNull InventoryClickEvent event;

    /**
     * The {@link MenuIcon} that was clicked in the menu.
     */
    private final @NotNull MenuIcon<M> icon;

    /**
     * The inventory slot of the icon ({@link MenuIcon}) that was clicked.
     */
    private final int iconSlot;

    /**
     * The page number of the menu that was clicked. (Default is 0 for non-paginated menus)
     */
    private final int page;

    @Internal
    public MenuClickData(
            @NotNull M menu,
            @NotNull Player player,
            @NotNull ClickType clickType,
            @NotNull InventoryClickEvent event,
            int page,
            @NotNull MenuIcon<M> icon,
            int iconSlot
    ) {
        this.menu = menu;
        this.player = player;
        this.clickType = clickType;
        this.event = event;
        this.page = page;
        this.icon = icon;
        this.iconSlot = iconSlot;
    }
}
