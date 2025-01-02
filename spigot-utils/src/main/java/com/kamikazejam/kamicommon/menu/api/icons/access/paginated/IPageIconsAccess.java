package com.kamikazejam.kamicommon.menu.api.icons.access.paginated;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.PrioritizedMenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StatefulIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StaticIconModifier;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuIconLoader;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IPageIconsAccess {
    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @NotNull
    default MenuIcon addPagedIcon(@NotNull IBuilder builder) {
        return this.addPagedIcon(new MenuIcon(true, builder));
    }
    @NotNull
    default MenuIcon addPagedIcon(@NotNull ItemStack stack) {
        return this.addPagedIcon(new MenuIcon(true, new ItemBuilder(stack)));
    }

    @NotNull
    default MenuIcon addPagedIcon(@NotNull String id, @NotNull IBuilder builder) {
        return this.addPagedIcon(new MenuIcon(true, builder).setId(id));
    }
    @NotNull
    default MenuIcon addPagedIcon(@NotNull String id, @NotNull ItemStack stack) {
        return this.addPagedIcon(new MenuIcon(true, new ItemBuilder(stack)).setId(id));
    }

    @NotNull
    default MenuIcon addPagedIcon(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        return this.addPagedIcon(MenuIconLoader.load(section.getConfigurationSection(key), player));
    }
    @NotNull
    default MenuIcon addPagedIcon(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addPagedIcon(MenuIconLoader.load(section.getConfigurationSection(key)));
    }
    @NotNull
    default MenuIcon addPagedIcon(@NotNull ConfigurationSection section, @Nullable Player player) {
        return this.addPagedIcon(MenuIconLoader.load(section, player));
    }
    @NotNull
    default MenuIcon addPagedIcon(@NotNull ConfigurationSection section) {
        return this.addPagedIcon(MenuIconLoader.load(section));
    }

    @NotNull MenuIcon addPagedIcon(@NotNull MenuIcon menuIcon);
    @NotNull MenuIcon addPagedIcon(@NotNull PrioritizedMenuIcon indexed);
    void clearPagedIcons();

    // ------------------------------------------------------------ //
    //                   Icon Management (by ID)                    //
    // ------------------------------------------------------------ //
    /**
     * Retrieve a paged icon by its id
     */
    @NotNull
    Optional<MenuIcon> getPagedIcon(@NotNull String id);

    @NotNull
    default IPageIconsAccess setMenuClick(@NotNull String id, @NotNull MenuClick click) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IPageIconsAccess setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IPageIconsAccess setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setMenuClick(click));
        return this;
    }
    @NotNull
    default IPageIconsAccess setModifier(@NotNull String id, @NotNull StaticIconModifier modifier) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IPageIconsAccess setModifier(@NotNull String id, @NotNull StatefulIconModifier modifier) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setModifier(modifier));
        return this;
    }
    @NotNull
    default IPageIconsAccess setAutoUpdate(@NotNull String id, @NotNull StaticIconModifier modifier, int tickInterval) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    @NotNull
    default IPageIconsAccess setAutoUpdate(@NotNull String id, @NotNull StatefulIconModifier modifier, int tickInterval) {
        this.getPagedIcon(id).ifPresent(icon -> icon.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    boolean isValidPagedIconID(@NotNull String id);
    @NotNull
    Set<String> getPagedIconIDs();
}
