package com.kamikazejam.kamicommon.menu.clicks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public interface PlayerSlotClick {
    /**
     * @param slot The player inventory slot, use {@link org.bukkit.inventory.PlayerInventory#getItem(int)} and {@link org.bukkit.inventory.PlayerInventory#setItem(int, org.bukkit.inventory.ItemStack)} to interact with the player's inventory.
     */
    void onClick(@NotNull Player player, @NotNull ClickType clickType, int slot);
}
