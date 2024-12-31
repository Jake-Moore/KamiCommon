package com.kamikazejam.kamicommon.menu.struct;

import com.kamikazejam.kamicommon.menu.callbacks.MenuOpenCallback;
import com.kamikazejam.kamicommon.menu.clicks.PlayerSlotClick;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A container for all the events that can be added to a {@link com.kamikazejam.kamicommon.menu.Menu}.<br>
 * The underlying {@link Map} can be accessed directly with getters for each callback type.<br>
 * Additional helper methods are provided to add callbacks.
 */
@Getter @Setter
@SuppressWarnings("unused")
public class MenuEvents {
    // All events are stored in a basic list, there is a getter if a user needs to remove or update the underlying list.
    private final @NotNull List<Predicate<InventoryClickEvent>> clickPredicates;
    private final @NotNull List<Consumer<InventoryCloseEvent>> closeConsumers;
    private final @NotNull List<Consumer<Player>> postCloseConsumers;
    private final @NotNull List<MenuOpenCallback> openCallbacks;
    // Player Clicks
    private final List<PlayerSlotClick> playerInvClicks;                    // List<Click>
    private final Map<Integer, List<PlayerSlotClick>> playerSlotClicks;     // Map<Slot, List<Click>>

    public MenuEvents() {
        this.clickPredicates = new ArrayList<>();
        this.closeConsumers = new ArrayList<>();
        this.postCloseConsumers = new ArrayList<>();
        this.openCallbacks = new ArrayList<>();
        this.playerInvClicks = new ArrayList<>();
        this.playerSlotClicks = new ConcurrentHashMap<>();
    }
    // Copy Constructor
    public MenuEvents(@NotNull MenuEvents copy) {
        this.clickPredicates = new ArrayList<>(copy.clickPredicates);
        this.closeConsumers = new ArrayList<>(copy.closeConsumers);
        this.postCloseConsumers = new ArrayList<>(copy.postCloseConsumers);
        this.openCallbacks = new ArrayList<>(copy.openCallbacks);
        this.playerInvClicks = new ArrayList<>(copy.playerInvClicks);
        this.playerSlotClicks = new ConcurrentHashMap<>(copy.playerSlotClicks);
    }

    /**
     * Add a predicate on {@link InventoryClickEvent} that must pass for any/all click handlers to be executed.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents addClickPredicate(@NotNull Predicate<InventoryClickEvent> predicate) {
        this.clickPredicates.add(predicate);
        return this;
    }

    /**
     * Add a consumer that runs when the inventory is closed, with access to {@link InventoryCloseEvent}.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents addCloseConsumer(@NotNull Consumer<InventoryCloseEvent> consumer) {
        this.closeConsumers.add(consumer);
        return this;
    }

    /**
     * Add a consumer that runs 1 tick after the inventory is closed, with access to {@link Player}.<br>
     * Note: This 1 tick delay is technically enough time for a player to log out. This event is guaranteed to run, but the player may not be online.<br>
     * This is the method you must use for Menus that you want to re-open when closed. Using {@link #addCloseConsumer(Consumer)} will cause recursion.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents addPostCloseConsumer(@NotNull Consumer<Player> consumer) {
        this.postCloseConsumers.add(consumer);
        return this;
    }

    /**
     * Add a callback that runs when the inventory is opened.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents addOpenCallback(@Nullable MenuOpenCallback menuOpen) {
        this.openCallbacks.add(menuOpen);
        return this;
    }

    /**
     * Listen to a player inventory click. (at a specific slot)
     * @param slot The player inventory slot number to listen to.
     * @return this {@link MenuEvents} object for chaining.
     */
    @NotNull
    public MenuEvents addPlayerSlotClick(int slot, @NotNull PlayerSlotClick click) {
        this.playerSlotClicks.computeIfAbsent(slot, k -> new ArrayList<>()).add(click);
        return this;
    }

    /**
     * Listen to ALL player inventory clicks. (in any slot)
     * @param click The callback to run when a player clicks a slot in their inventory.
     * @return this {@link MenuEvents} object for chaining.
     */
    @NotNull
    public MenuEvents addPlayerSlotClick(@NotNull PlayerSlotClick click) {
        this.playerInvClicks.add(click);
        return this;
    }

    @NotNull
    public MenuEvents copy() {
        return new MenuEvents(this);
    }
}
