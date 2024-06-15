package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings("unused")
public class MenuHolder implements InventoryHolder {

    public transient Inventory inventory;
    @Getter @Setter private String invName;
    @Getter @Setter private int rows;
    @Getter @Setter private @Nullable InventoryType type;

    public MenuHolder() {}

    public MenuHolder(String name, int rows) {
        this.invName = StringUtil.t(name);
        this.rows = rows;
        this.type = null;
    }

    public MenuHolder(String name, @Nonnull InventoryType type) {
        this.invName = StringUtil.t(name);
        this.rows = -1;
        this.type = type;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            if (type != null) {
                this.inventory = Bukkit.createInventory(this, type, invName);
            }else {
                this.inventory = Bukkit.createInventory(this, rows * 9, invName);
            }

            if (invName.length() > 32) {
                PluginSource.warning("Inventory name is too long! (" + invName.length() + " > 32)");
            }
        }
        return this.inventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuHolder that = (MenuHolder) o;
        return Objects.equals(getInventory(), that.getInventory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInventory());
    }

    public InventoryHolder getHolder() { return this; }
}
