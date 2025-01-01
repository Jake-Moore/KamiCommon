package com.kamikazejam.kamicommon.menu.api.icons.access;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuIconLoader;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface IMenuIconsAccess {
    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    default MenuIcon addMenuIcon(@NotNull IBuilder builder, int slot) {
        return this.addMenuIcon(new MenuIcon(true, new StaticIconSlot(slot), builder));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull IBuilder builder, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, slot, builder));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ItemStack stack, int slot) {
        return this.addMenuIcon(new MenuIcon(true, new StaticIconSlot(slot), new ItemBuilder(stack)));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ItemStack stack, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, slot, new ItemBuilder(stack)));
    }

    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull IBuilder builder, int slot) {
        return this.addMenuIcon(new MenuIcon(true, new StaticIconSlot(slot), builder).setId(id));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull ItemStack stack, int slot) {
        return this.addMenuIcon(new MenuIcon(true, new StaticIconSlot(slot), new ItemBuilder(stack)).setId(id));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull IBuilder builder, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, slot, builder).setId(id));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull String id, @NotNull ItemStack stack, @NotNull IconSlot slot) {
        return this.addMenuIcon(new MenuIcon(true, slot, new ItemBuilder(stack)).setId(id));
    }

    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        return this.addMenuIcon(MenuIconLoader.load(section.getConfigurationSection(key), player));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addMenuIcon(MenuIconLoader.load(section.getConfigurationSection(key)));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section, @Nullable Player player) {
        return this.addMenuIcon(MenuIconLoader.load(section, player));
    }
    @NotNull
    default MenuIcon addMenuIcon(@NotNull ConfigurationSection section) {
        return this.addMenuIcon(MenuIconLoader.load(section));
    }

    @NotNull
    MenuIcon addMenuIcon(@NotNull MenuIcon menuIcon);

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
    default IMenuIconsAccess setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IMenuIconsAccess setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
        this.getMenuIcon(id).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    boolean isValidMenuIconID(@NotNull String id);
    @NotNull
    Set<String> getMenuIconIDs();
}
