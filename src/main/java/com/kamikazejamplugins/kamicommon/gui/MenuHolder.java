package com.kamikazejamplugins.kamicommon.gui;

import com.kamikazejamplugins.kamicommon.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

@SuppressWarnings("unused")
public class MenuHolder implements InventoryHolder {

    private transient Inventory inventory;
    @Getter @Setter private String invName;
    @Getter @Setter private int lines;
    @Getter @Setter private InventoryType inventoryType;

    public MenuHolder() {}

    public MenuHolder(String name) {
        this.invName = name;
    }

    public MenuHolder(String name, int lines) {
        this.invName = StringUtil.t(name);
        this.lines = lines;
    }

    public MenuHolder(String name, InventoryType inventoryType) {
        this.invName = name;
        this.inventoryType = inventoryType;
    }

    @Override
    public Inventory getInventory() {
        if (this.inventory == null) {
            if (this.inventoryType == null) {
                this.inventory = Bukkit.createInventory(this, lines * 9, invName);
                //this.inventory = Bukkit.createInventory(this, lines * 9, StringUtils.abbreviate(invName, 32));
            } else {
                this.inventory = Bukkit.createInventory(this, inventoryType, invName);
                //this.inventory = Bukkit.createInventory(this, inventoryType, StringUtils.abbreviate(invName, 32));
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
