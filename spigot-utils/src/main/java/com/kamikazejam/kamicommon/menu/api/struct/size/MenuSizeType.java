package com.kamikazejam.kamicommon.menu.api.struct.size;

import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MenuSizeType implements MenuSize {
    private static final KamiSet<InventoryType> NOT_SUPPORTED = new KamiSet<>(
            InventoryType.CREATIVE,
            InventoryType.CRAFTING,
            InventoryType.MERCHANT
    );
    private final @NotNull InventoryType type;

    public MenuSizeType(@NotNull InventoryType type) {
        Preconditions.checkNotNull(type, "Type must not be null.");
        Preconditions.checkArgument(!NOT_SUPPORTED.contains(type), "InventoryType " + type.name() + " is not supported by KamiCommon menus.");
        this.type = type;
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder holder, @NotNull String title) {
        return Bukkit.createInventory(holder, type, title);
    }

    @Override
    public int getSlotInLastRow(int index) {
        // Basic Chest Calculation based on 9-slot rows, like (9x3) or (9x6)
        if (type.getDefaultSize() >= 27 && type.getDefaultSize() % 9 == 0) {
            if (index < 0 || index > 8) {
                return -1;
            }
            return type.getDefaultSize() - (9 - index);
        }
        // For menus with 3-slot rows (3x3)
        if (type.getDefaultSize() == 9) {
            if (index < 0 || index > 2) {
                return -1;
            }
            return InventoryType.DISPENSER.getDefaultSize() - (3 - index);
        }

        // Otherwise the best we can do is just use the index as the slot
        if (index < 0 || index >= type.getDefaultSize()) {
            return -1;
        }
        return index;
    }

    @Override
    public @NotNull MenuSize copy() {
        return new MenuSizeType(type);
    }

    @Override
    public int getNumberOfSlots() {
        return type.getDefaultSize();
    }

    @Override
    public int mapPositionToSlot(int row, int col) throws IllegalArgumentException, IllegalStateException {
        return switch (type.name()) {
            // Our 9x3 menus
            case "CHEST", "ENDER_CHEST", "BARREL", "SHULKER_BOX":
                yield MenuSizeRows.mapPositionToSlot(row, col, 3);
            case "PLAYER":
                // Player inventory is 4 rows, with weird mapping (hotbar is the first 9 indexes)
                if (row < 0 || row >= 4 || col < 0 || col >= 9) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in a 4 row player inventory.");
                }
                // If we're on row 3, we use hotbar slot numbers
                if (row == 3) {
                    yield col; // Hotbar slots are 0-8
                }
                // Otherwise we use the normal 9-slot rows, but where the first one is 9 in bukkit math
                yield row * 9 + col + 9;
            // Our 3x3 menus
            case "DISPENSER", "DROPPER", "CRAFTER":
                if (row < 0 || row >= 3 || col < 0 || col >= 3) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in a 3x3 menu.");
                }
                yield row * 3 + col;
            case "WORKBENCH":
                // We support (1, 3) as an exception to mark the crafting result slot
                // (0, 3) and (2, 3) are not allowed though, as there is only 1 possible 4th column slot (result slot)
                if (row < 0 || row >= 3 || col < 0 || (row == 1 ? col >= 4 : col >= 3)) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in a 3x3 workbench.");
                }
                // The (1, 3) case is actually slot 0 in bukkit
                if (row == 1 && col == 3) {
                    yield 0;
                }
                // The other slots are shifted by 1 but in a normal 3x3 layout
                yield row * 3 + col + 1;
            case "ENCHANTING": // 2 slots horizontally
                if (row != 0 || col < 0 || col >= 2) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in an enchanting table.");
                }
                yield col;
            case "ANVIL": // 3 slots horizontally
                if (row != 0 || col < 0 || col >= 3) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in an anvil.");
                }
                yield col;
            case "BEACON": // 1 slot
                if (row != 0 || col != 0) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in a beacon.");
                }
                yield 0;
            case "HOPPER": // 5 slots horizontally
                if (row != 0 || col < 0 || col >= 5) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in a hopper.");
                }
                yield col;
            case "SMITHING": // 4 slots horizontally
                if (row != 0 || col < 0 || col >= 4) {
                    throw new IllegalArgumentException("Cannot map position to slot for x=" + row + ", y=" + col + " in a smithing table.");
                }
                yield col;

            // Nonsensical Types we can't decide how to support
            case "FURNACE", "BLAST_FURNACE", "CRAFTING", "BREWING", "LECTERN", "SMOKER", "LOOM",
                 "STONECUTTER", "CARTOGRAPHY", "GRINDSTONE", "COMPOSTER", "CHISELED_BOOKSHELF", "JUKEBOX", "DECORATED_POT":
                throw new IllegalStateException("Cannot map position to slot for InventoryType " + type.name() + ". (not supported)");
            default:
                throw new IllegalStateException("Cannot map position to slot for InventoryType " + type.name() + ". (unknown)");
        };
    }
}
