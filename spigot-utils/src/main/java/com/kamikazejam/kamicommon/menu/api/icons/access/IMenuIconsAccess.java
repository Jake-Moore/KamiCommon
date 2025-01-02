package com.kamikazejam.kamicommon.menu.api.icons.access;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StatefulIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StaticIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.menu.api.loaders.IconSlotLoader;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuIconLoader;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

// TODO ADD METHODS RETRIEVING ICONS BY SLOT
// TODO ADD METHODS TO BOTH SET AND RETRIEVE ICONS BY POINT
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IMenuIconsAccess {
    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    default MenuIcon addMenuIcon(@NotNull IBuilder builder, int slot) {
        return this.addMenuIcon(new MenuIcon(true, builder), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull IBuilder builder, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, builder), slot);
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ItemStack stack, int slot) {
        return this.addMenuIcon(new MenuIcon(true, new ItemBuilder(stack)), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ItemStack stack, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, new ItemBuilder(stack)), slot);
    }

    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull IBuilder builder, int slot) {
        return this.addMenuIcon(new MenuIcon(true, builder).setId(id), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull ItemStack stack, int slot) {
        return this.addMenuIcon(new MenuIcon(true, new ItemBuilder(stack)).setId(id), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull IBuilder builder, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, builder).setId(id), slot);
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull ItemStack stack, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, new ItemBuilder(stack)).setId(id), slot);
    }

    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        ConfigurationSection iconSection = section.getConfigurationSection(key);
        return this.addMenuIcon(section, player);
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addMenuIcon(section, key, null);
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section, @Nullable Player player) {
        MenuIcon icon = MenuIconLoader.load(section, player);
        IconSlot slot = IconSlotLoader.load(section);
        return this.addMenuIcon(icon, slot);
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section) {
        return this.addMenuIcon(section, (Player) null);
    }

    @NotNull
    MenuIcon addMenuIcon(@NotNull MenuIcon menuIcon, @Nullable IconSlot slot);

    @Nullable
    MenuIcon removeMenuIcon(@NotNull String id);

    void clearMenuIcons();



    // ------------------------------------------------------------ //
    //                   Icon Management (by ID)                    //
    // ------------------------------------------------------------ //

    /**
     * Retrieve a {@link MenuIcon} by its id
     */
    @NotNull
    Optional<MenuIcon> getMenuIcon(@NotNull String id);

    @NotNull
    default IMenuIconsAccess setMenuClick(@NotNull String id, @NotNull MenuClick click) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setModifier(@NotNull String id, @NotNull StaticIconModifier modifier) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setModifier(@NotNull String id, @NotNull StatefulIconModifier modifier) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setAutoUpdate(@NotNull String id, @NotNull StaticIconModifier modifier, int tickInterval) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setAutoUpdate(@NotNull String id, @NotNull StatefulIconModifier modifier, int tickInterval) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    boolean isValidMenuIconID(@NotNull String id);
    @NotNull
    Set<String> getMenuIconIDs();



    // ------------------------------------------------------------ //
    //                  Icon Management (by slot)                   //
    // ------------------------------------------------------------ //
    /**
     * Retrieve a {@link MenuIcon} by a slot number. If the filler MenuIcon has been configured and is enabled,
     * it will be returned if no other MenuIcon is found for the slot.
     */
    @NotNull
    Optional<MenuIcon> getMenuIconForSlot(int slot);

    @NotNull
    default IMenuIconsAccess setMenuClickForSlot(int slot, @NotNull MenuClick click) {
        this.getMenuIconForSlot(slot).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setMenuClickForSlot(int slot, @NotNull MenuClickEvent click) {
        this.getMenuIconForSlot(slot).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setModifierForSlot(int slot, @NotNull StaticIconModifier modifier) {
        this.getMenuIconForSlot(slot).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setModifierForSlot(int slot, @NotNull StatefulIconModifier modifier) {
        this.getMenuIconForSlot(slot).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setAutoUpdateForSlot(int slot, @NotNull StaticIconModifier modifier, int tickInterval) {
        this.getMenuIconForSlot(slot).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setAutoUpdateForSlot(int slot, @NotNull StatefulIconModifier modifier, int tickInterval) {
        this.getMenuIconForSlot(slot).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    boolean hasMenuIconForSlot(int slot);

}
