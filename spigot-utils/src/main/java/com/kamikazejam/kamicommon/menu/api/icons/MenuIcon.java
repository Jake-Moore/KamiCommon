package com.kamikazejam.kamicommon.menu.api.icons;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.paginated.PaginatedMenuClickPageTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.simple.SimpleMenuClickEventTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.simple.SimpleMenuClickTransform;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StatefulIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.StaticIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier.MenuIconModifier;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a menu icon that can have several builders and slots<br>
 * This class also holds the click data for the icon, if one is set
 */
@Getter @Setter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MenuIcon {
    @Setter(AccessLevel.NONE)
    private @NotNull String id = UUID.randomUUID().toString(); // final because it's used in HashMaps

    private @Nullable ItemStack lastItem;
    private @Nullable IClickTransform transform = null;
    private @Nullable Integer updateInterval = null;
    private @Nullable MenuIconModifier modifier = null;

    // Allow this icon to be enabled or disabled (if it should be put in the menu)
    private boolean enabled;
    // Allow multiple possible builders, which can be cycled through
    private final @NotNull List<IBuilder> iBuilders = new ArrayList<>();
    private int builderRotateTicks = 20; // Default to 1 second
    // Allow multiple possible slots
    private @Nullable IconSlot iconSlot;

    // Icon Click Sound Fields
    private @NotNull XSound clickSound = XSound.UI_BUTTON_CLICK;
    private int clickVolume = 1;
    private int clickPitch = 2;

    // --------------------------------------------- //
    //                  Constructors                 //
    // --------------------------------------------- //

    public MenuIcon(@NotNull IBuilder builder, int slot) {
        this(true, builder, slot);
    }
    public MenuIcon(boolean enabled, @NotNull IBuilder builder, int slot) {
        this.enabled = enabled;
        this.iBuilders.add(builder);
        this.iconSlot = new StaticIconSlot(slot);
    }
    public MenuIcon(@NotNull IBuilder builder, @NotNull List<Integer> slots) {
        this(true, builder, slots);
    }
    public MenuIcon(boolean enabled, @NotNull IBuilder builder, @NotNull List<Integer> slots) {
        this.enabled = enabled;
        this.iBuilders.add(builder);
        this.iconSlot = new StaticIconSlot(slots);
    }
    public MenuIcon(@NotNull IBuilder builder, @NotNull Integer... slots) {
        this(true, builder, slots);
    }
    public MenuIcon(boolean enabled, @NotNull IBuilder builder, @NotNull Integer... slots) {
        this(enabled, builder, Arrays.asList(slots));
    }
    public MenuIcon(int slot, @NotNull IBuilder... builders) {
        this(true, slot, builders);
    }
    public MenuIcon(boolean enabled, int slot, @NotNull IBuilder... builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(Arrays.asList(builders));
        this.iconSlot = new StaticIconSlot(slot);
    }
    public MenuIcon(@NotNull List<Integer> slots, @NotNull IBuilder... builders) {
        this(true, slots, builders);
    }
    public MenuIcon(boolean enabled, @NotNull List<Integer> slots, @NotNull IBuilder... builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(Arrays.asList(builders));
        this.iconSlot = new StaticIconSlot(slots);
    }
    public MenuIcon(@Nullable IconSlot slot, @NotNull IBuilder... builders) {
        this(true, slot, builders);
    }
    public MenuIcon(boolean enabled, @Nullable IconSlot slot, @NotNull IBuilder... builders) {
        this.enabled = enabled;
        this.iconSlot = slot;
        this.iBuilders.addAll(Arrays.asList(builders));
    }
    public MenuIcon(int slot, @NotNull Collection<IBuilder> builders) {
        this(true, slot, builders);
    }
    public MenuIcon(boolean enabled, int slot, @NotNull Collection<IBuilder> builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(builders);
        this.iconSlot = new StaticIconSlot(slot);
    }
    public MenuIcon(@NotNull List<Integer> slots, @NotNull Collection<IBuilder> builders) {
        this(true, slots, builders);
    }
    public MenuIcon(boolean enabled, @NotNull List<Integer> slots, @NotNull Collection<IBuilder> builders) {
        this.enabled = enabled;
        this.iBuilders.addAll(builders);
        this.iconSlot = new StaticIconSlot(slots);
    }
    public MenuIcon(@Nullable IconSlot slot, @NotNull Collection<IBuilder> builders) {
        this(true, slot, builders);
    }
    public MenuIcon(boolean enabled, @Nullable IconSlot slot, @NotNull Collection<IBuilder> builders) {
        this.enabled = enabled;
        this.iconSlot = slot;
        this.iBuilders.addAll(builders);
    }

    @NotNull
    public MenuIcon copy() {
        MenuIcon copy;
        if (this.iconSlot != null) {
            copy = new MenuIcon(this.enabled, this.iconSlot.copy(), this.iBuilders);
        }else {
            copy = new MenuIcon(this.enabled, (IconSlot) null, this.iBuilders);
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
    //                 MenuIcon Methods              //
    // --------------------------------------------- //
    public void playClickSound(@NotNull Player player) {
        clickSound.play(player, clickVolume, clickPitch);
    }
    public @NotNull MenuIcon setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    @NotNull
    public Set<Integer> getSlots(@NotNull Menu menu) {
        if (iconSlot == null) { return Collections.emptySet(); }
        return iconSlot.get(menu);
    }

    public @NotNull MenuIcon setAutoUpdate(@NotNull StaticIconModifier modifier, int tickInterval) {
        this.updateInterval = tickInterval;
        this.modifier = modifier;
        return this;
    }
    public @NotNull MenuIcon setAutoUpdate(@NotNull StatefulIconModifier modifier, int tickInterval) {
        this.updateInterval = tickInterval;
        this.modifier = modifier;
        return this;
    }

    public @NotNull MenuIcon setMenuClick(@NotNull MenuClick click) {
        this.transform = new SimpleMenuClickTransform(click);
        return this;
    }

    public @NotNull MenuIcon setMenuClick(@NotNull MenuClickPage click) {
        this.transform = new PaginatedMenuClickPageTransform(click);
        return this;
    }

    public @NotNull MenuIcon setMenuClick(@NotNull MenuClickEvent click) {
        this.transform = new SimpleMenuClickEventTransform(click);
        return this;
    }

    public @NotNull MenuIcon setModifier(@Nullable StaticIconModifier modifier) {
        this.modifier = modifier;
        return this;
    }
    public @NotNull MenuIcon setModifier(@Nullable StatefulIconModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    // --------------------------------------------- //
    //        Methods Regarding Auto Updating        //
    // --------------------------------------------- //

    public final @Nullable ItemStack buildItem(boolean cycleToNextBuilder) {
        final int pre = this.builderIndex;

        // Find the existing ItemStack (if available) so that modifications can reference the current item
        //  while building the new state on the IBuilder (which is based on the initial state)
        @Nullable IBuilder current = this.getCurrentBuilder();
        @Nullable ItemStack currentItem = current != null ? current.build() : null;

        @Nullable IBuilder initialBuilder = cycleToNextBuilder ? getNextBuilder() : getCurrentBuilder();
        if (initialBuilder == null) { return null; }

        // Modify the builder if needed
        if (modifier instanceof StaticIconModifier builderModifier) {
            builderModifier.modify(initialBuilder);
        }else if (modifier instanceof StatefulIconModifier updateModifier) {
            updateModifier.modify(initialBuilder, currentItem);
        }
        return initialBuilder.build();
    }

    public boolean isCycleBuilderForTick(int tick) {
        // If we need to supply a new IBuilder from MenuIcon, then we should update
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

    // --------------------------------------------- //
    //                 Object Comparison             //
    // --------------------------------------------- //

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!(obj instanceof MenuIcon menuIcon)) { return false; }
        return enabled == menuIcon.enabled
                && iBuilders.equals(menuIcon.iBuilders)
                && Objects.equals(iconSlot, menuIcon.iconSlot)
                && Objects.equals(transform, menuIcon.transform)
                && Objects.equals(updateInterval, menuIcon.updateInterval)
                && Objects.equals(modifier, menuIcon.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, iBuilders, iconSlot, transform, updateInterval, modifier);
    }


    public static MenuIcon getDefaultFillerIcon() {
        return new MenuIcon(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName(" "), -1).setId("filler");
    }
}

