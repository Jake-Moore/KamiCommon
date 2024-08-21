package com.kamikazejam.kamicommon.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class MenuItem {

    private ItemStack item;
    private int slot;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof MenuItem menuItem)) { return false; }
        return slot == menuItem.slot && Objects.equals(item, menuItem.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, item);
    }
}

