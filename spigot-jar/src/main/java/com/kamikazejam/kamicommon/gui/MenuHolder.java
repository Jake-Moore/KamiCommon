package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * InventoryHolder with constructor parameters for making an Inventory with either a row count or an InventoryType.<br>
 * Also contains a few utility methods for better utilization with the KamiCommon library.
 */
@SuppressWarnings("unused")
public class MenuHolder implements InventoryHolder {

    protected transient @Nullable Inventory inventory;
    @Getter @Setter private @NotNull String invName;
    @Getter @Setter private int rows;
    @Getter @Setter private @Nullable InventoryType type;

    public MenuHolder() {}

    public MenuHolder(@NotNull String name, int rows) {
        this.invName = StringUtil.t(name);
        this.rows = rows;
        this.type = null;
    }

    public MenuHolder(@NotNull String name, @Nonnull InventoryType type) {
        this.invName = StringUtil.t(name);
        this.rows = -1;
        this.type = type;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            if (invName.length() > 32) {
                PluginSource.warning("Inventory name is too long! (" + invName.length() + " > 32)");
            }

            // Create the new inventory, preferring to use the Type over slot count
            return this.inventory = (type != null)
                    ? Bukkit.createInventory(this, type, invName)
                    : Bukkit.createInventory(this, rows * 9, invName);
        }
        return this.inventory;
    }

    public InventoryHolder getHolder() {
        return this;
    }



    // --------------------------------------------------------------------- //
    //                       Inventory Access Methods                        //
    // --------------------------------------------------------------------- //
    public int getSize() {
        return this.getInventory().getSize();
    }

    @Nullable
    public ItemStack getItem(int slot) {
        return this.getInventory().getItem(slot);
    }

    public void setItem(int slot, @Nullable ItemStack item) {
        // prevent inventory null pointers
        if (slot < 0) { return; }

        this.getInventory().setItem(slot, item);
    }

    public void setItem(int slot, @Nullable IBuilder builder) {
        this.setItem(slot, builder == null ? null : builder.toItemStack());
    }

    public void clear() {
        this.getInventory().clear();
    }

    public int firstEmpty() {
        return this.getInventory().firstEmpty();
    }

    public int firstEmpty(@NotNull Collection<Integer> slots) {
        return firstEmpty(slots.stream().mapToInt(i -> i).toArray());
    }

    public int firstEmpty(int[] slots) {
        int size = this.getSize();
        for (int slot : slots) {
            try {
                if (slot > size) {
                    throw new IllegalStateException("Slot couldn't fit in this inventory size.");
                }
                @Nullable ItemStack slotStack = this.getItem(slot);
                if (slotStack == null || XMaterial.matchXMaterial(slotStack) == XMaterial.AIR) {
                    return slot;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }



    // --------------------------------------------------------------------- //
    //                           Object Comparison                           //
    // --------------------------------------------------------------------- //
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
