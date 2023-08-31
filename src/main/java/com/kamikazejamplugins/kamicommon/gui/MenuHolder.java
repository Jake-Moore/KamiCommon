package com.kamikazejamplugins.kamicommon.gui;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class MenuHolder implements InventoryHolder {

    private transient Inventory inventory;
    @Getter @Setter private String invName;
    @Getter @Setter private int rows;

    public MenuHolder() {}

    public MenuHolder(String name) {
        this.invName = name;
    }

    public MenuHolder(String name, int rows) {
        this.invName = StringUtil.t(name);
        this.rows = rows;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, rows * 9, invName);
            if (invName.length() > 32) {
                KamiCommon.get().getLogger().warning("Inventory name is too long! (" + invName.length() + " > 32)");
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
}
