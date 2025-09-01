package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.api.MenuHolder;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.access.MenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.util.ItemUtil;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@Accessors(chain = true)
@SuppressWarnings({"UnusedReturnValue", "unused"})
public sealed abstract class AbstractMenu<M extends AbstractMenu<M>> extends MenuHolder implements Menu<M>, UpdatingMenu permits SimpleMenu, PaginatedMenu, OneClickMenu {
    protected final Player player;
    // priority icon is used to keep track of the order icons were registered, which is necessary when resizing
    // The data type PriorityMenuIcon<M> also keeps track of the slot data
    protected final @NotNull PrioritizedMenuIconMap<M> menuIcons;
    // Configuration
    protected final MenuEvents<M> events;
    protected final MenuOptions<M> options;

    // Internal Data
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // Constructor (Deep Copying from Builder)
    protected AbstractMenu(@NotNull AbstractMenuBuilder<M, ?> builder, @NotNull Player player) {
        super(builder.size.copy(), builder.titleCalculator.buildTitle(player));
        this.player = player;
        this.menuIcons = builder.menuIcons.copy();
        this.events = builder.events.copy();
        this.options = builder.options.copy();
    }

    @Override
    public void reopenMenu() {
        // Sanity Checks
        if (!PlayerUtil.isFullyValidPlayer(player) || !player.getUniqueId().equals(this.player.getUniqueId())) {return;}
        this.open(false);
    }

    @Override
    public void reopenMenu(boolean resetTickCounter) {
        // Sanity Checks
        if (!PlayerUtil.isFullyValidPlayer(player) || !player.getUniqueId().equals(this.player.getUniqueId())) {return;}
        this.open(resetTickCounter);
    }

    /**
     * Open the {@link Inventory} for the {@link Player} that this menu was created for.
     * @return The {@link InventoryView} for the new menu, or null if the player was not online to open the menu for.
     */
    @Nullable
    public InventoryView open() {
        return this.open(options.isResetVisualsOnOpen());
    }

    /**
     * Open the {@link Inventory} for the {@link Player} that this menu was created for.
     * @param resetTickCounter If true, the tick counter will be reset to 0 when opening the menu.
     * @return The {@link InventoryView} for the new menu, or null if the player was not online to open the menu for.
     */
    @Nullable
    public InventoryView open(boolean resetTickCounter) {
        return this.openInternal(resetTickCounter);
    }

    private InventoryView openInternal(boolean resetTickCounter) {
        if (!PlayerUtil.isFullyValidPlayer(player)) {return null;}

        // Reset the tick counter for icons that auto update if necessary
        if (resetTickCounter) {
            tickCounter.set(0);
        }

        // Place all icons into the inventory
        // This method will also handle the filler icon placement
        placeIcons(null);

        // Register this menu for auto-updating
        SpigotUtilsSource.getMenuManager().getAutoUpdateInventories().add(this);

        // Open the Menu for the Player
        InventoryView view = Objects.requireNonNull(player.openInventory(this.getInventory()));
        events.getOpenCallbacks().values().forEach(callback -> callback.onOpen(player, view));
        return view;
    }

    /**
     * Close the {@link Inventory} for the {@link Player} that this menu was created for.<br>
     * Identical to calling {@link Player#closeInventory()}.
     * @return If the inventory was successfully closed. False if the player is no longer valid (not online).
     */
    public boolean close() {
        if (!PlayerUtil.isFullyValidPlayer(player)) {return false;}

        player.closeInventory();
        return true;
    }

    @Override
    public void setSize(@NotNull MenuSize size) {
        this.resizeMenu(size);
    }

    public void resizeMenu(@NotNull MenuSize size) {
        // Because PrioritizedMenuIcon<M> keeps the IconSlot, and the PrioritizedMenuIconMap uses the MenuSize
        // every time it needs to derive the slot for a MenuIcon, all we need to do is update the MenuSize (& update MenuHolder)
        // The next time icons are placed, it will automatically re-calculate the slots for each icon
        // and then the priorities (which are based on the order of registration) will be used
        super.size = size.copy();

        // clear existing Inventory (since we must remake a new one)
        @Nullable final Inventory oldInv = this.getRawInventory();
        super.deleteInventory();

        // Re-open the inventory for players that were viewing it
        if (oldInv != null && !oldInv.getViewers().isEmpty()) {
            // We need to prevent the close event from triggering its close callbacks
            this.events.getIgnoreNextInventoryCloseEvent().set(true);
            // This will trigger the previous Inventory to close, hence the need to ignore the events
            openInternal(false);
        }
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //

    @SuppressWarnings("unchecked")
    public @NotNull M modifyIcons(@NotNull Consumer<IMenuIconsAccess<M>> consumer) {
        consumer.accept(this.getMenuIconsAccess());
        return (M) this;
    }

    @Override
    public @NotNull Map<String, MenuIcon<M>> getMenuIcons() {
        return menuIcons.getMenuIcons();
    }

    @Override
    public @NotNull IMenuIconsAccess<M> getMenuIconsAccess() {
        return new MenuIconsAccess<>(this.getMenuSize(), this.menuIcons);
    }

    @Override
    public @Nullable MenuIcon<M> getFillerIcon() {
        return this.menuIcons.get("filler").orElse(null);
    }

    // ------------------------------------------------------------ //
    //                   Icon Update Management                     //
    // ------------------------------------------------------------ //
    @ApiStatus.Internal
    @Override
    public void updateOneTick() {
        // getAndIncrement means we start at 1, since 0th tick should have been the call to open()
        int tick = this.tickCounter.incrementAndGet();
        this.placeIcons((m) -> m.needsModification(tick));
    }

    /**
     * Manually trigger an update for all icons matching this predicate.<br>
     * If the predicate is passed, the icon will be re-built and set in the inventory.<br>
     * If the predicate is null, it will always update all icons.
     * @param needsUpdate An optional predicate to filter which icons need new builders.
     */
    public void placeIcons(@Nullable Predicate<MenuIcon<M>> needsUpdate) {
        int tick = this.tickCounter.get();

        // Keep track of the current state of the menu (as a map of each slot to an ItemStack)
        // Initialize to all nulls (i.e. all empty slots)
        Map<Integer, @Nullable ItemStack> newMenuState = new HashMap<>();
        for (int i = 0; i < this.getSize(); i++) {
            newMenuState.put(i, null);
        }

        // Fill Inventory Slots with Known MenuIcons
        final MenuSize size = this.getMenuSize();
        Set<Integer> forFillerSlots = new HashSet<>();
        for (int i = 0; i < this.getSize(); i++) {
            // If forHere is not null, then it is guaranteed to be enabled
            @Nullable MenuIcon<M> forHere = this.menuIcons.getActiveIconForSlot(size, i);
            @Nullable ItemStack lastItem = (forHere == null) ? null : forHere.getLastItem();

            // 1. If there is no icon for this slot, we need to fill it with the filler icon
            if (forHere == null) {
                forFillerSlots.add(i);
                continue;
            }

            // 2. If there is an icon here, and it does not need updating, we skip it
            // "does not need updating" = needs no tick update AND is still in the GUI as expected
            boolean stillItemHere = lastItem != null && ItemUtil.isSimplySimilar(lastItem, this.getItem(i));
            if (needsUpdate != null && !needsUpdate.test(forHere) && stillItemHere) {
                // by removing from new state, it won't get included in the loop that places items
                // therefore skipping it entirely and leaving that slot alone
                newMenuState.remove(i);
                continue;
            }

            // 3. Rebuild the ItemStack for this icon, and record this item
            //  Note the behavior here. the Icon can return a null builder, which we will assume
            //   was intended, and set the slot to null (making it an empty inventory slot)
            //  We do not add the slot to the emptySlots set, because we don't want to fill it with the filler icon
            @Nullable ItemStack item = forHere.buildItem(tick, this.player);
            forHere.setLastItem(item);

            // 4. Record the new item in the new menu state
            newMenuState.put(i, item);
        }

        // Fill with filler (always - ignores predicate) in the slots that do not have a MenuIcon
        placeFiller(newMenuState, forFillerSlots, tick);

        // Update the inventory with the new menu state
        for (Map.Entry<Integer, @Nullable ItemStack> entry : newMenuState.entrySet()) {
            this.setItem(entry.getKey(), entry.getValue());
        }
    }

    protected void placeFiller(Map<Integer, @Nullable ItemStack> newMenuState, Set<Integer> slots, int tick) {
        // First, apply the excluded slots filter to the slots set
        slots.removeAll(this.options.getExcludedFillSlots());

        // Skip fill if the filler icon is disabled or not found
        @Nullable MenuIcon<?> icon = this.menuIcons.getOrDefault("filler", null);
        if (icon == null || !icon.isEnabled()) {
            return;
        }

        // Determine if we need to update this filler icon
        @Nullable ItemStack fillerItem = icon.buildItem(tick, this.player);

        // Place the filler icon in the slots
        slots.forEach(slot -> newMenuState.put(slot, fillerItem));
    }
}
