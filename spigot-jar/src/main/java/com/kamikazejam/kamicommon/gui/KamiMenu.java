package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.gui.interfaces.*;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenu extends AbstractKamiMenu {
    private @Nullable PageBuilder<?> parent = null;

    public KamiMenu(String name, int lines) {
        this(name, lines, null);
    }
    public KamiMenu(String name, InventoryType type) {
        this(name, type, null);
    }
    public KamiMenu(String name, int lines, @Nullable PageBuilder<?> parent) {
        super(name, lines);
        this.parent = parent;
    }
    public KamiMenu(String name, InventoryType type, @Nullable PageBuilder<?> parent) {
        super(name, type);
        this.parent = parent;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        addMenuClick(stack, (MenuClick) null, slot);
    }

    @Override
    public void setItem(int slot, IBuilder stack) {
        setItem(slot, stack.toItemStack());
    }

    @Override
    public void setItem(int slot, IBuilder stack, Player forPlaceholders) {
        setItem(slot, stack.build(forPlaceholders));
    }

    @Override
    public void addMenuClick(ItemStack stack, MenuClick click, int slot) {
        addSpecialMenuClick(stack, new MenuClickTransform(click), slot);
    }

    @Override
    public void addMenuClick(ItemStack stack, MenuClickPlayer click, int slot) {
        addSpecialMenuClick(stack, new MenuClickPlayerTransform(click), slot);
    }

    @Override
    public void addMenuClick(ItemStack stack, MenuClickPlayerPage click, int slot) {
        addSpecialMenuClick(stack, new MenuClickPlayerPageTransform(click), slot);
    }

    @Override
    public void addSpecialMenuClick(ItemStack stack, MenuClickInfo click, int slot) {
        // prevent inventory null pointers
        if (slot < 0 || stack == null) { return; }

        // limit max amount to 64.
        if (stack.getAmount() > 64) { stack.setAmount(64); }

        if (click != null) {
            getClickableItems().put(new MenuItem(stack, slot), click);
        }

        getInventory().setItem(slot, stack);
    }

    public ItemStack getDefaultFiller() {
        XMaterial mat = XMaterial.GRAY_STAINED_GLASS_PANE;
        return new ItemBuilder(mat, 1, mat.getData()).setName(" ").toItemStack();
    }

    public KamiMenu fill() {
        fill(getDefaultFiller());
        return this;
    }

    public KamiMenu fill(ItemStack fillerItem) {
        while (getInventory().firstEmpty() != -1) {
            addMenuClick(fillerItem, (MenuClick) null, getInventory().firstEmpty());
        }
        return this;
    }

    public KamiMenu fill(IBuilder iBuilder) {
        if (iBuilder == null) {
            try { throw new Exception("iBuilder is null in fill(iBuilder). Using default filler!");
            }catch (Throwable t) { t.printStackTrace(); }
            return fill();
        }
        return fill(iBuilder.toItemStack());
    }
}
