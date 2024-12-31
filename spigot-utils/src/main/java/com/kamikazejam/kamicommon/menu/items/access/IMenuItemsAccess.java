package com.kamikazejam.kamicommon.menu.items.access;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.items.MenuItem;
import com.kamikazejam.kamicommon.menu.items.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.menu.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.menu.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.menu.loaders.MenuItemLoader;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface IMenuItemsAccess {
    // ------------------------------------------------------------ //
    //                        Item Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    default MenuItem addMenuItem(@NotNull IBuilder builder, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull IBuilder builder, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, builder));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull ItemStack stack, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull ItemStack stack, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, new ItemBuilder(stack)));
    }

    @NotNull
    default MenuItem addMenuItem(@NotNull String id, @NotNull IBuilder builder, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder).setId(id));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull String id, @NotNull ItemStack stack, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)).setId(id));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull String id, @NotNull IBuilder builder, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, builder).setId(id));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull String id, @NotNull ItemStack stack, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, new ItemBuilder(stack)).setId(id));
    }

    @NotNull
    default MenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key), player));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key)));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull ConfigurationSection section, @Nullable Player player) {
        return this.addMenuItem(MenuItemLoader.load(section, player));
    }
    @NotNull
    default MenuItem addMenuItem(@NotNull ConfigurationSection section) {
        return this.addMenuItem(MenuItemLoader.load(section));
    }

    @NotNull
    MenuItem addMenuItem(@NotNull MenuItem menuItem);

    @Nullable
    MenuItem removeMenuItem(@NotNull String id);

    void clearMenuItems();



    // ------------------------------------------------------------ //
    //                   Item Management (by ID)                    //
    // ------------------------------------------------------------ //

    /**
     * Retrieve a menu item by its id
     */
    @NotNull
    Optional<MenuItem> getMenuItem(@NotNull String id);

    @NotNull
    default IMenuItemsAccess setMenuClick(@NotNull String id, @NotNull MenuClick click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    default IMenuItemsAccess setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    default IMenuItemsAccess setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
        this.getMenuItem(id).ifPresent(item -> item.setModifier(modifier));
        return this;
    }
    @NotNull
    default IMenuItemsAccess setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
        this.getMenuItem(id).ifPresent(item -> item.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    boolean isValidMenuItemID(@NotNull String id);
    @NotNull
    Set<String> getMenuItemIDs();
}
