package com.kamikazejam.kamicommon.menu.api;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * InventoryHolder with constructor parameters for making an Inventory with either a row count or an InventoryType.<br>
 * Also contains a few utility methods for better utilization with the KamiCommon library.
 */
@Getter @Setter
@SuppressWarnings("unused")
public class MenuHolder implements InventoryHolder {

    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    protected transient @Nullable Inventory inventory;

    private @NotNull String title;
    private @NotNull MenuSize size;

    public MenuHolder(@NotNull MenuSize size, @Nullable String name) {
        this.size = size;
        this.title = (name == null) ? " " : StringUtil.t(name);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            return this.inventory = this.size.createInventory(this, title);
        }
        return this.inventory;
    }

    public void closeAll(@NotNull Set<UUID> exceptions) {
        this.getViewers().forEach(humanEntity -> {
            if (!exceptions.contains(humanEntity.getUniqueId())) {
                humanEntity.closeInventory();
            }
        });
    }

    @NotNull
    public List<HumanEntity> getViewers() {
        if (this.inventory == null) {
            return new ArrayList<>();
        }
        return this.inventory.getViewers();
    }

    @NotNull
    public InventoryHolder getHolder() {
        return this;
    }



    // --------------------------------------------------------------------- //
    //                       Inventory Access Methods                        //
    // --------------------------------------------------------------------- //
    public int getSize() {
        return this.getInventory().getSize();
    }
    @NotNull
    public MenuSize getMenuSize() {
        return this.size;
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

    public void replaceTitle(@NotNull String find, @NotNull String replacement) {
        Preconditions.checkNotNull(find, "find cannot be null");
        Preconditions.checkNotNull(replacement, "replacement cannot be null");
        title = title.replace(find, replacement);
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
