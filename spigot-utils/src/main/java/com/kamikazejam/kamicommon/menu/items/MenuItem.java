package com.kamikazejam.kamicommon.menu.items;

import com.cryptomorin.xseries.XSound;
import com.kamikazejam.kamicommon.menu.KamiMenu;
import com.kamikazejam.kamicommon.menu.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.menu.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.clicks.transform.MenuClickEventTransform;
import com.kamikazejam.kamicommon.menu.clicks.transform.MenuClickPageTransform;
import com.kamikazejam.kamicommon.menu.clicks.transform.MenuClickTransform;
import com.kamikazejam.kamicommon.menu.items.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.menu.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.menu.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.item.IBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a menu item that can have several builders and slots<br>
 * This class does not include any click data, it just provides items
 */
@Getter @Setter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MenuItem {
    @Setter(AccessLevel.NONE)
    private @NotNull String id = UUID.randomUUID().toString(); // final because it's used in HashMaps

    private @Nullable ItemStack lastItem;
    private @Nullable IClickTransform transform = null;
    private @Nullable Integer updateInterval = null;
    private @Nullable IBuilderModifier modifier = null;

    // Allow this item to be enabled or disabled (if it should be put in the menu)
    private boolean enabled;
    // Allow multiple possible items
    private final @NotNull List<IBuilder> iBuilders = new ArrayList<>();
    private int builderRotateTicks = 20; // Default to 1 second
    // Allow multiple possible slots
    private @Nullable ItemSlot itemSlot;

    // Item Click Sound Fields
    private @NotNull XSound clickSound = XSound.UI_BUTTON_CLICK;
    private int clickVolume = 1;
    private int clickPitch = 2;

    // --------------------------------------------- //
    //                  Constructors                 //
    // --------------------------------------------- //

    public MenuItem(@NotNull IBuilder builder, int slot) {
        this(true, builder, slot);
    }
    public MenuItem(boolean enabled, @NotNull IBuilder builder, int slot) {
        this.enabled = enabled;
        this.iBuilders.add(builder);
        this.itemSlot = new StaticItemSlot(slot);
    }
    public MenuItem(@NotNull IBuilder builder, @NotNull List<Integer> slots) {
        this(true, builder, slots);
    }
    public MenuItem(boolean enabled, @NotNull IBuilder builder, @NotNull List<Integer> slots) {
        this.enabled = enabled;
        this.iBuilders.add(builder);
        this.itemSlot = new StaticItemSlot(slots);
    }
    public MenuItem(@NotNull IBuilder builder, @NotNull Integer... slots) {
        this(true, builder, slots);
    }
    public MenuItem(boolean enabled, @NotNull IBuilder builder, @NotNull Integer... slots) {
        this(enabled, builder, Arrays.asList(slots));
    }
    public MenuItem(int slot, @NotNull IBuilder... builders) {
        this(true, slot, builders);
    }
    public MenuItem(boolean enabled, int slot, @NotNull IBuilder... builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(Arrays.asList(builders));
        this.itemSlot = new StaticItemSlot(slot);
    }
    public MenuItem(@NotNull List<Integer> slots, @NotNull IBuilder... builders) {
        this(true, slots, builders);
    }
    public MenuItem(boolean enabled, @NotNull List<Integer> slots, @NotNull IBuilder... builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(Arrays.asList(builders));
        this.itemSlot = new StaticItemSlot(slots);
    }
    public MenuItem(@Nullable ItemSlot slot, @NotNull IBuilder... builders) {
        this(true, slot, builders);
    }
    public MenuItem(boolean enabled, @Nullable ItemSlot slot, @NotNull IBuilder... builders) {
        this.enabled = enabled;
        this.itemSlot = slot;
        this.iBuilders.addAll(Arrays.asList(builders));
    }
    public MenuItem(int slot, @NotNull Collection<IBuilder> builders) {
        this(true, slot, builders);
    }
    public MenuItem(boolean enabled, int slot, @NotNull Collection<IBuilder> builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(builders);
        this.itemSlot = new StaticItemSlot(slot);
    }
    public MenuItem(@NotNull List<Integer> slots, @NotNull Collection<IBuilder> builders) {
        this(true, slots, builders);
    }
    public MenuItem(boolean enabled, @NotNull List<Integer> slots, @NotNull Collection<IBuilder> builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(builders);
        this.itemSlot = new StaticItemSlot(slots);
    }
    public MenuItem(@Nullable ItemSlot slot, @NotNull Collection<IBuilder> builders) {
        this(true, slot, builders);
    }
    public MenuItem(boolean enabled, @Nullable ItemSlot slot, @NotNull Collection<IBuilder> builders) {
        this.enabled = enabled;
        this.itemSlot = slot;
        this.iBuilders.addAll(builders);
    }

    @NotNull
    public MenuItem copy() {
        MenuItem copy;
        if (this.itemSlot != null) {
            copy = new MenuItem(this.enabled, this.itemSlot.copy(), this.iBuilders);
        }else {
            copy = new MenuItem(this.enabled, (ItemSlot) null, this.iBuilders);
        }
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
    //                 MenuItem Methods              //
    // --------------------------------------------- //
    public void playClickSound(@NotNull Player player) {
        clickSound.play(player, clickVolume, clickPitch);
    }
    public @NotNull MenuItem setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    @NotNull
    public Set<Integer> getSlots(@NotNull KamiMenu menu) {
        if (itemSlot == null) { return Collections.emptySet(); }
        return itemSlot.get(menu);
    }

    // --------------------------------------------- //
    //                    IMenuItem                  //
    // --------------------------------------------- //

    public @NotNull MenuItem setAutoUpdate(@NotNull IBuilderModifier modifier, int tickInterval) {
        this.updateInterval = tickInterval;
        this.modifier = modifier;
        return this;
    }

    public @NotNull MenuItem setMenuClick(@NotNull MenuClick click) {
        this.transform = new MenuClickTransform(click);
        return this;
    }

    public @NotNull MenuItem setMenuClick(@NotNull MenuClickPage click) {
        this.transform = new MenuClickPageTransform(click);
        return this;
    }

    public @NotNull MenuItem setMenuClick(@NotNull MenuClickEvent click) {
        this.transform = new MenuClickEventTransform(click);
        return this;
    }

    public @NotNull MenuItem setModifier(@Nullable IBuilderModifier modifier) {
        this.modifier = modifier;
        this.updateInterval = null;
        return this;
    }

    // --------------------------------------------- //
    //              Tickable Properties              //
    // --------------------------------------------- //

    public final @Nullable ItemStack buildItem(boolean newBuilder) {
        final int pre = this.builderIndex;
        @Nullable IBuilder base = newBuilder ? getNextBuilder() : getCurrentBuilder();
        if (base == null) { return null; }

        // Modify the item
        if (modifier != null) {
            modifier.modify(base);
        }
        return base.build();
    }

    public boolean isCycleBuilderForTick(int tick) {
        // If we need to supply a new IBuilder from MenuItem, then we should update
        return this.iBuilders.size() > 1 && this.getBuilderRotateTicks() > 0 && tick % this.getBuilderRotateTicks() == 0;
    }

    public boolean isAutoUpdateForTick(int tick) {
        return updateInterval != null && updateInterval > 0 && tick % updateInterval == 0;
    }

    public boolean needsModification(int tick) {
        return isCycleBuilderForTick(tick) || isAutoUpdateForTick(tick);
    }

    // --------------------------------------------- //
    //               IBuilder Methods                //
    // --------------------------------------------- //

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int builderIndex = 0;

    @Nullable
    public IBuilder getNextBuilder() {
        if (iBuilders.isEmpty()) { return null; }
        this.builderIndex++;
        if (this.builderIndex >= iBuilders.size()) {
            this.builderIndex = 0;
        }
        return iBuilders.get(this.builderIndex).clone();
    }

    @Nullable
    public IBuilder getCurrentBuilder() {
        if (iBuilders.isEmpty()) { return null; }
        if (this.builderIndex >= iBuilders.size()) {
            this.builderIndex = 0;
        }
        return iBuilders.get(this.builderIndex).clone();
    }

    @ApiStatus.Internal
    public void directModifyBuilders(@NotNull IBuilderModifier modifier) {
        iBuilders.forEach(modifier::modify);
    }

    // --------------------------------------------- //
    //                 Object Comparison             //
    // --------------------------------------------- //

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!(obj instanceof MenuItem menuItem)) { return false; }
        return enabled == menuItem.enabled
                && iBuilders.equals(menuItem.iBuilders)
                && Objects.equals(itemSlot, menuItem.itemSlot)
                && Objects.equals(transform, menuItem.transform)
                && Objects.equals(updateInterval, menuItem.updateInterval)
                && Objects.equals(modifier, menuItem.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, iBuilders, itemSlot, transform, updateInterval, modifier);
    }
}

