package com.kamikazejamplugins.kamicommon.gui;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.gui.interfaces.*;
import com.kamikazejamplugins.kamicommon.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenu<T extends Player> extends AbstractKamiMenu<T> {

    public KamiMenu(String name, int lines) {
        super(name, lines);
        if (KamiCommon.getPlugin() == null) {
            throw new RuntimeException("KamiCommon plugin is not initialized properly. Please call KamiCommon.setupPlugin(this) in your onEnable method. Menu title: '" + name + "'");
        }
    }

    public void clear() {
        getInventory().clear();
        getClickableItems().clear();
    }

    @Override
    public void update() {
        MenuUpdate menuUpdate = getUpdateHandler();

        if (menuUpdate != null) {
            clear();
            menuUpdate.onUpdate();
        }
    }

    public void addItem(List<ItemStack> items) {
        items.forEach(i -> getInventory().addItem(i));
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        addMenuClick(stack, (MenuClick) null, slot);
    }

    @Override
    public void setItem(int slot, ItemBuilder stack) {
        setItem(slot, stack.toItemStack());
    }

    @Override
    public void addMenuClick(ItemStack stack, MenuClick click, int slot) {
        addSpecialMenuClick(stack, new MenuClickTransform<>(click), slot);
    }

    @Override
    public void addMenuClick(ItemStack stack, MenuClickPlayer<T> click, int slot) {
        addSpecialMenuClick(stack, new MenuClickPlayerTransform<>(click), slot);
    }

    @Override
    public void addSpecialMenuClick(ItemStack stack, MenuClickInfo<T> click, int slot) {
        // prevent inventory null pointers
        if (slot < 0 || stack == null) {
            return;
        }

        // limit max amount to 64.
        if (stack.getAmount() > 64) stack.setAmount(64);

        if (click != null) {
            getClickableItems().put(new MenuItem(stack, slot), click);
        }

        getInventory().setItem(slot, stack);
    }

    public ItemStack getDefaultFiller() {
        XMaterial mat = XMaterial.GRAY_STAINED_GLASS_PANE;
        return new ItemBuilder(mat, 1, mat.getData()).setName(" ").toItemStack();
    }

    public KamiMenu<T> fill() {
        fill(getDefaultFiller());
        return this;
    }

    public KamiMenu<T> fill(ItemStack fillerItem) {
        while (getInventory().firstEmpty() != -1) {
            addMenuClick(fillerItem, (MenuClick) null, getInventory().firstEmpty());
        }
        return this;
    }
}
