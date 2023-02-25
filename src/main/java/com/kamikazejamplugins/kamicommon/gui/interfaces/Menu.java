package com.kamikazejamplugins.kamicommon.gui.interfaces;

import com.kamikazejamplugins.kamicommon.gui.MenuItem;
import com.kamikazejamplugins.kamicommon.item.IBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "SameReturnValue"})
public interface Menu<T extends Player> extends InventoryHolder {

    Inventory getInventory();

    int getSize();

    ItemStack getItem(int i);

    Map<MenuItem, MenuClickInfo<T>> getClickableItems();

    void addMenuClick(ItemStack stack, MenuClick click, int slot);

    default void addMenuClick(IBuilder builder, MenuClick click, int slot) {
        addMenuClick(builder.toItemStack(), click, slot);
    }

    void addMenuClick(ItemStack stack, MenuClickPlayer<T> click, int slot);

    default void addMenuClick(IBuilder builder, MenuClickPlayer<T> click, int slot) {
        addMenuClick(builder.toItemStack(), click, slot);
    }

    default void addSpecialMenuClick(IBuilder builder, MenuClickInfo<T> click, int slot) {
        addSpecialMenuClick(builder.toItemStack(), click, slot);
    }

    void addSpecialMenuClick(ItemStack stack, MenuClickInfo<T> click, int slot);

    void setItem(int slot, ItemStack stack);

    default void setItem(int slot, IBuilder stack) {
        setItem(slot, stack.toItemStack());
    }

    default void setItem(ItemStack stack, int slot) {
        setItem(slot, stack);
    }

    default void setItem(IBuilder stack, int slot) {
        setItem(slot, stack.toItemStack());
    }

    default void setItem(IBuilder stack, int slot, Player forPlaceholders) {
        setItem(slot, stack.toItemStack(forPlaceholders));
    }

    Set<String> getIgnoredClose();

    MenuUpdate getUpdateHandler();

    void addMenuClick(IBuilder builder, MenuClick click, int slot, Player forPlaceholders);

    void addMenuClick(IBuilder builder, MenuClickPlayer<T> click, int slot, Player forPlaceholders);

    void setUpdateHandler(MenuUpdate update);

    void update();

    void setAutoUpdate();

    void openMenu(Player player);

    void openMenu(Player player, boolean ignoreCloseHandler);

    int firstEmpty(int[] slots);

    Predicate<InventoryClickEvent> getClickHandler();

    void setClickHandler(Predicate<InventoryClickEvent> consumer);

    default void setClickHandler(Consumer<InventoryClickEvent> consumer) {
        this.setClickHandler(predicate -> {
            consumer.accept(predicate);
            return true;
        });
    }

    Consumer<InventoryCloseEvent> getCloseHandler();

    void setCloseHandler(Consumer<InventoryCloseEvent> consumer);

    Consumer<InventoryCloseEvent> getInstantCloseHandler();

    void setInstantCloseHandler(Consumer<InventoryCloseEvent> consumer);

    void closeInventory(Player member);

    void closeInventory(Player player, boolean onlyCloseOne);

    default boolean allowItemPickup() {
        return true;
    }

    void setAllowItemPickup(boolean itemPickup);

}
