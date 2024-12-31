package com.kamikazejam.kamicommon.menu.struct.size;

import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MenuSizeType implements MenuSize {
    private final @NotNull InventoryType type;
    public MenuSizeType(@NotNull InventoryType type) {
        Preconditions.checkNotNull(type, "Type must not be null.");
        this.type = type;
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder holder, @NotNull String title) {
        return Bukkit.createInventory(holder, type, title);
    }

    @Override
    public int getSlotInLastRow(int index) {
        // Basic Chest Calculation based on 9-slot rows (9x3) or (9x6)
        if (type.getDefaultSize() >= 27 && type.getDefaultSize() % 9 == 0) {
            if (index < 0 || index > 8) { return -1; }
            return type.getDefaultSize() - (9 - index);
        }
        // For menus with 3-slot rows (3x3)
        if (type.getDefaultSize() == 9) {
            if (index < 0 || index > 2) { return -1; }
            return InventoryType.DISPENSER.getDefaultSize() - (3 - index);
        }

        // Otherwise the best we can do is just use the index as the slot
        if (index < 0 || index >= type.getDefaultSize()) { return -1; }
        return index;
    }

    @Override
    public @NotNull MenuSize copy() {
        return new MenuSizeType(type);
    }
}
