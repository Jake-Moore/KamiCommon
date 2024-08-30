package com.kamikazejam.kamicommon.gui.struct;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter @Setter
public class MenuSize {
    private @Nullable Integer rows;
    private @Nullable InventoryType type;
    public MenuSize(int rows) {
        Preconditions.checkArgument(rows > 0, "Rows must be greater than 0.");
        Preconditions.checkArgument(rows < 7, "Rows must be less than 7.");
        this.rows = rows;
        this.type = null;
    }
    public MenuSize(@NotNull InventoryType type) {
        this.type = type;
        this.rows = null;
    }

    @NotNull
    public Inventory createInventory(@NotNull InventoryHolder holder, @NotNull String title) {
        // Prefer creating with Type over row count
        if (type != null) {
            return Bukkit.createInventory(holder, type, title);
        }
        if (rows != null) {
            return Bukkit.createInventory(holder, rows * 9, title);
        }
        throw new IllegalStateException("Inventory size not set.");
    }

    public void setRows(int rows) {
        Preconditions.checkArgument(rows > 0, "Rows must be greater than 0.");
        Preconditions.checkArgument(rows < 7, "Rows must be less than 7.");
        this.rows = rows;
        this.type = null;
    }

    public void setType(@NotNull InventoryType type) {
        this.type = type;
        this.rows = null;
    }
}
