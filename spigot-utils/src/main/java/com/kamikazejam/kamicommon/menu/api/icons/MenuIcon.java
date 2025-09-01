package com.kamikazejam.kamicommon.menu.api.icons;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.MenuClickTransform;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.MenuIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StatefulIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StaticIconModifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu icon that can contains the {@link ItemStack} data as {@link ItemBuilder}<br>
 * This class also holds the click data for the icon, and the auto updating logic for the icon
 */
@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MenuIcon<M extends Menu<M>> {
    @Setter(AccessLevel.NONE)
    private @NotNull String id = UUID.randomUUID().toString(); // final because it's used in HashMaps

    private @Nullable ItemStack lastItem;
    private @Nullable MenuClickTransform<M> transform = null;
    private @Nullable Integer updateInterval = null;
    private @Nullable MenuIconModifier modifier = null;

    // Allow this icon to be enabled or disabled (if it should be put in the menu)
    private boolean enabled;
    // Allow multiple possible builders, which can be cycled through
    private final @NotNull List<ItemBuilder> itemBuilders = new ArrayList<>();
    private int builderRotateTicks = 20; // Default to 1 second

    // Icon Click Sound Fields
    private @NotNull XSound clickSound = XSound.UI_BUTTON_CLICK;
    private int clickVolume = 1;
    private int clickPitch = 2;

    // --------------------------------------------- //
    //                  Constructors                 //
    // --------------------------------------------- //

    public MenuIcon(@NotNull ItemBuilder builder) {
        this(true, builder);
    }

    public MenuIcon(boolean enabled, @NotNull ItemBuilder builder) {
        this.enabled = enabled;
        this.itemBuilders.add(builder);
    }

    public MenuIcon(boolean enabled, @NotNull ItemBuilder... builders) {
        this.enabled = enabled;
        this.itemBuilders.addAll(Arrays.asList(builders));
    }

    public MenuIcon(boolean enabled, @NotNull Collection<ItemBuilder> builders) {
        this.enabled = enabled;
        this.itemBuilders.addAll(builders);
    }

    @NotNull
    public MenuIcon<M> copy() {
        MenuIcon<M> copy = new MenuIcon<>(this.enabled, this.itemBuilders);
        copy.id = this.id;
        copy.lastItem = this.lastItem;
        copy.transform = this.transform;
        copy.updateInterval = this.updateInterval;
        copy.modifier = this.modifier;
        copy.builderRotateTicks = this.builderRotateTicks;
        copy.clickSound = this.clickSound;
        copy.clickVolume = this.clickVolume;
        copy.clickPitch = this.clickPitch;
        return copy;
    }

    // --------------------------------------------- //
    //                 MenuIcon<M> Methods              //
    // --------------------------------------------- //
    public void playClickSound(@NotNull Player player) {
        clickSound.play(player, clickVolume, clickPitch);
    }

    public @NotNull MenuIcon<M> setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    public @NotNull MenuIcon<M> setAutoUpdate(@NotNull StaticIconModifier modifier, int tickInterval) {
        this.updateInterval = tickInterval;
        this.modifier = modifier;
        return this;
    }

    public @NotNull MenuIcon<M> setAutoUpdate(@NotNull StatefulIconModifier modifier, int tickInterval) {
        this.updateInterval = tickInterval;
        this.modifier = modifier;
        return this;
    }

    public @NotNull MenuIcon<M> setMenuClick(@NotNull MenuClick<M> click) {
        this.transform = new MenuClickTransform<>(click);
        return this;
    }

    public @NotNull MenuIcon<M> setModifier(@Nullable StaticIconModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    public @NotNull MenuIcon<M> setModifier(@Nullable StatefulIconModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    // --------------------------------------------- //
    //        Methods Regarding Auto Updating        //
    // --------------------------------------------- //
    @ApiStatus.Internal
    public final @Nullable ItemStack buildItem(int tick, @NotNull Player player) {
        final int pre = this.builderIndex;
        boolean cycleToNextBuilder = tick > 0 && this.isCycleBuilderForTick(tick);

        @Nullable ItemBuilder next = cycleToNextBuilder ? getNextBuilder() : getCurrentBuilder();
        if (next == null) {return null;}

        // Modify the builder
        if (modifier instanceof StaticIconModifier builderModifier) {
            builderModifier.modify(next);
        } else if (modifier instanceof StatefulIconModifier updateModifier) {
            // Use the existing ItemStack (if available) so that stateful modifications can reference it
            //  while building the state of the new ItemBuilder (which is a copy of the initial configuration)
            updateModifier.modify(next, this.getLastItem(), player, tick);
        }

        ItemStack stack = next.build();
        if (stack.getAmount() > 64) { stack.setAmount(64); }
        return stack;
    }

    @ApiStatus.Internal
    public final boolean isCycleBuilderForTick(int tick) {
        // If we need to supply a new ItemBuilder from MenuIcon, then we should update
        return this.itemBuilders.size() > 1 && this.getBuilderRotateTicks() > 0 && tick % this.getBuilderRotateTicks() == 0;
    }

    @ApiStatus.Internal
    public final boolean isAutoUpdateForTick(int tick) {
        return updateInterval != null && updateInterval > 0 && tick % updateInterval == 0;
    }

    @ApiStatus.Internal
    public final boolean needsModification(int tick) {
        return isCycleBuilderForTick(tick) || isAutoUpdateForTick(tick);
    }

    // --------------------------------------------- //
    //              ItemBuilder Methods              //
    // --------------------------------------------- //

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int builderIndex = 0;

    @Nullable
    public ItemBuilder getNextBuilder() {
        if (itemBuilders.isEmpty()) {return null;}
        this.builderIndex++;
        if (this.builderIndex >= itemBuilders.size()) {
            this.builderIndex = 0;
        }
        return itemBuilders.get(this.builderIndex).clone();
    }

    @Nullable
    public ItemBuilder getCurrentBuilder() {
        if (itemBuilders.isEmpty()) {return null;}
        if (this.builderIndex >= itemBuilders.size()) {
            this.builderIndex = 0;
        }
        return itemBuilders.get(this.builderIndex).clone();
    }

    // --------------------------------------------- //
    //                 Object Comparison             //
    // --------------------------------------------- //

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (!(obj instanceof MenuIcon<?> menuIcon)) {return false;}
        return enabled == menuIcon.enabled
                && itemBuilders.equals(menuIcon.itemBuilders)
                && Objects.equals(transform, menuIcon.transform)
                && Objects.equals(updateInterval, menuIcon.updateInterval)
                && Objects.equals(modifier, menuIcon.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, itemBuilders, transform, updateInterval, modifier);
    }

    @NotNull
    public static <M extends Menu<M>> MenuIcon<M> getDefaultFillerIcon() {
        return new MenuIcon<M>(Config.getDefaultFillerIconBuilder()).setId("filler");
    }

    /**
     * Basic configuration for MenuIcon defaults.
     */
    @Configurable
    public static class Config {
        @Getter @Setter
        private static @NotNull ItemBuilder defaultFillerIconBuilder = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName(" ");
    }
}

