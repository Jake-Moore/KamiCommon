package com.kamikazejam.kamicommon.menu.api.clicks.data;

import com.kamikazejam.kamicommon.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Data class to hold information about a click event occurring inside a {@link Player}'s {@link Inventory}.
 * This is a click that occurs while the given {@link #getMenu()} is open for the player.
 */
@Getter
public class PlayerClickData<M extends Menu<M>> {
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
     * The inventory slot that was clicked (slot from the player inventory).
     */
    private final int slot;

    public PlayerClickData(
            @NotNull M menu,
            @NotNull Player player,
            @NotNull ClickType clickType,
            @NotNull InventoryClickEvent event,
            int slot
    ) {
        this.menu = menu;
        this.player = player;
        this.clickType = clickType;
        this.event = event;
        this.slot = slot;
    }
}
