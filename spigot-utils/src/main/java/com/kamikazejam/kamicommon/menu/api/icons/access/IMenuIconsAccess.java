package com.kamikazejam.kamicommon.menu.api.icons.access;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StatefulIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StaticIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.PositionSlot;
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

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IMenuIconsAccess {
    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    default MenuIcon setMenuIcon(@NotNull IBuilder builder, int slot) {
        return this.setMenuIcon(new MenuIcon(true, builder), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull IBuilder builder, @NotNull IconSlot slot) {
        return this.setMenuIcon(new MenuIcon(true, builder), slot);
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull ItemStack stack, int slot) {
        return this.setMenuIcon(new MenuIcon(true, new ItemBuilder(stack)), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull ItemStack stack, @NotNull IconSlot slot) {
        return this.setMenuIcon(new MenuIcon(true, new ItemBuilder(stack)), slot);
    }

    @NotNull
    default MenuIcon setMenuIcon(@NotNull String id, @NotNull IBuilder builder, int slot) {
        return this.setMenuIcon(new MenuIcon(true, builder).setId(id), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull String id, @NotNull ItemStack stack, int slot) {
        return this.setMenuIcon(new MenuIcon(true, new ItemBuilder(stack)).setId(id), new StaticIconSlot(slot));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull String id, @NotNull IBuilder builder, @NotNull IconSlot slot) {
        return this.setMenuIcon(new MenuIcon(true, builder).setId(id), slot);
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull String id, @NotNull ItemStack stack, @NotNull IconSlot slot) {
        return this.setMenuIcon(new MenuIcon(true, new ItemBuilder(stack)).setId(id), slot);
    }

    @NotNull
    default MenuIcon setMenuIcon(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        ConfigurationSection iconSection = section.getConfigurationSection(key);
        return this.setMenuIcon(section, player);
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.setMenuIcon(section, key, null);
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull ConfigurationSection section, @Nullable Player player) {
        MenuIcon icon = MenuIconLoader.load(section, player);
        IconSlot slot = IconSlotLoader.load(section);
        return this.setMenuIcon(icon, slot);
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull ConfigurationSection section) {
        return this.setMenuIcon(section, (Player) null);
    }

    /**
     * @return The same {@link MenuIcon} for chaining.
     */
    @NotNull
    MenuIcon setMenuIcon(@NotNull MenuIcon menuIcon, @Nullable IconSlot slot);

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
    default IMenuIconsAccess setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
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
    Optional<MenuIcon> getMenuIcon(int slot);

    boolean hasMenuIcon(int slot);


    // ------------------------------------------------------------ //
    //                  Icon Management (by position)                  //
    // ------------------------------------------------------------ //
    @NotNull
    default MenuIcon setMenuIcon(@NotNull IBuilder builder, int row, int col) {
        return this.setMenuIcon(new MenuIcon(true, builder), new PositionSlot(row, col));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull ItemStack stack, int row, int col) {
        return this.setMenuIcon(new MenuIcon(true, new ItemBuilder(stack)), new PositionSlot(row, col));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull String id, @NotNull IBuilder builder, int row, int col) {
        return this.setMenuIcon(new MenuIcon(true, builder).setId(id), new PositionSlot(row, col));
    }
    @NotNull
    default MenuIcon setMenuIcon(@NotNull String id, @NotNull ItemStack stack, int row, int col) {
        return this.setMenuIcon(new MenuIcon(true, new ItemBuilder(stack)).setId(id), new PositionSlot(row, col));
    }

    /**
     * Retrieve a {@link MenuIcon} by a position. If the filler MenuIcon has been configured and is enabled,
     * it will be returned if no other MenuIcon is found for the position.
     * @param row The row of the position (top to bottom) (1-indexed)
     * @param col The column of the position (left to right) (1-indexed)
     */
    @NotNull
    default Optional<MenuIcon> getMenuIcon(int row, int col) {
        return this.getMenuIcon(new PositionSlot(row, col));
    }

    /**
     * Retrieve a {@link MenuIcon} by a position. If the filler MenuIcon has been configured and is enabled,
     * it will be returned if no other MenuIcon is found for the position.
     * @param slot The position slot
     */
    @NotNull
    Optional<MenuIcon> getMenuIcon(@NotNull PositionSlot slot);

    /**
     * @param row The row of the position (top to bottom) (1-indexed)
     * @param col The column of the position (left to right) (1-indexed)
     */
    default boolean hasMenuIcon(int row, int col) {
        return this.hasMenuIcon(new PositionSlot(row, col));
    }

    /**
     * @param slot The position slot
     */
    boolean hasMenuIcon(@NotNull PositionSlot slot);

}
