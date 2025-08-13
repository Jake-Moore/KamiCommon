package com.kamikazejam.kamicommon.menu.api.struct;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.callbacks.MenuCloseCallback;
import com.kamikazejam.kamicommon.menu.api.callbacks.MenuOpenCallback;
import com.kamikazejam.kamicommon.menu.api.callbacks.MenuPostCloseCallback;
import com.kamikazejam.kamicommon.menu.api.clicks.PlayerSlotClick;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * A container for all the events that can be added to a {@link com.kamikazejam.kamicommon.menu.Menu}.<br>
 * The underlying {@link Map} can be accessed directly with getters for each callback type.<br>
 * Additional helper methods are provided to add callbacks.
 */
@Getter
@Setter
@SuppressWarnings("unused")
public class MenuEvents<M extends Menu<M>> {

    // All events are stored in a map of id->object, this is so that they can be inserted and removed by IDs
    private final @NotNull Map<String, Predicate<InventoryClickEvent>> clickPredicates;
    private final @NotNull Map<String, MenuCloseCallback> closeCallbacks;
    private final @NotNull Map<String, MenuPostCloseCallback<M>> postCloseCallbacks;
    private final @NotNull Map<String, MenuOpenCallback> openCallbacks;
    // Player Clicks
    private final Map<String, PlayerSlotClick<M>> playerInvClicks;                            // List<Click>              (processed before per-slot clicks)
    private final Map<Integer, Map<String, PlayerSlotClick<M>>> playerSlotClicks;             // Map<Slot, List<Click>>   (processed after global clicks)
    private final Map<String, Predicate<InventoryClickEvent>> playerInvClickPredicates;
    // Ability to ignore upcoming events
    private final @NotNull AtomicBoolean ignoreNextInventoryCloseEvent;

    public MenuEvents() {
        this.clickPredicates = new HashMap<>();
        this.closeCallbacks = new HashMap<>();
        this.postCloseCallbacks = new HashMap<>();
        this.openCallbacks = new HashMap<>();
        this.playerInvClicks = new HashMap<>();
        this.playerSlotClicks = new ConcurrentHashMap<>();
        this.playerInvClickPredicates = new HashMap<>();
        this.ignoreNextInventoryCloseEvent = new AtomicBoolean(false);
    }

    // Copy Constructor
    private MenuEvents(@NotNull MenuEvents<M> copy) {
        this.clickPredicates = new HashMap<>(copy.clickPredicates);
        this.closeCallbacks = new HashMap<>(copy.closeCallbacks);
        this.postCloseCallbacks = new HashMap<>(copy.postCloseCallbacks);
        this.openCallbacks = new HashMap<>(copy.openCallbacks);
        this.playerInvClicks = new HashMap<>(copy.playerInvClicks);
        this.playerSlotClicks = new ConcurrentHashMap<>(copy.playerSlotClicks);
        this.playerInvClickPredicates = new HashMap<>(copy.playerInvClickPredicates);
        this.ignoreNextInventoryCloseEvent = new AtomicBoolean(copy.ignoreNextInventoryCloseEvent.get());
    }

    /**
     * Add a predicate on {@link InventoryClickEvent} that must pass for the click handlers on that slot to be executed.<br>
     * This applies to the {@link com.kamikazejam.kamicommon.menu.Menu}'s inventory only, not the player's inventory.<br>
     * For adding a predicate for player inventory clicks, use {@link #addPlayerClickPredicate(Predicate)}
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addClickPredicate(@NotNull Predicate<InventoryClickEvent> predicate) {
        this.clickPredicates.put(UUID.randomUUID().toString(), predicate);
        return this;
    }

    /**
     * Add a predicate on {@link InventoryClickEvent} that must pass for the click handlers on that slot to be executed.<br>
     * This applies to the {@link com.kamikazejam.kamicommon.menu.Menu}'s inventory only, not the player's inventory.<br>
     * For adding a predicate for player inventory clicks, use {@link #addPlayerClickPredicate(String, Predicate)}
     * @param id The ID to associate with this predicate for later removal
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addClickPredicate(@NotNull String id, @NotNull Predicate<InventoryClickEvent> predicate) {
        this.clickPredicates.put(id, predicate);
        return this;
    }

    /**
     * Remove a click predicate by ID
     */
    public boolean removeClickPredicate(@NotNull String id) {
        return this.clickPredicates.remove(id) != null;
    }

    /**
     * Add a callback that runs when the inventory is closed, with access to {@link Player} and {@link InventoryCloseEvent}.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addCloseCallback(@NotNull MenuCloseCallback callback) {
        this.closeCallbacks.put(UUID.randomUUID().toString(), callback);
        return this;
    }

    /**
     * Add a callback that runs when the inventory is closed, with access to {@link Player} and {@link InventoryCloseEvent}.
     * @param id The ID to associate with this callback for later removal
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addCloseCallback(@NotNull String id, @NotNull MenuCloseCallback callback) {
        this.closeCallbacks.put(id, callback);
        return this;
    }

    /**
     * Remove a close callback by ID
     */
    public boolean removeCloseCallback(@NotNull String id) {
        return this.closeCallbacks.remove(id) != null;
    }

    /**
     * Add a callback that runs 1 tick after the inventory is closed, with access to {@link Player}.<br>
     * Note: This 1 tick delay is technically enough time for a player to log out. This event is guaranteed to run, but the player may not be online.<br>
     * This is the method you must use for Menus that you want to re-open when closed. Using {@link #addCloseCallback(MenuCloseCallback)} will cause recursion.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addPostCloseCallback(@NotNull MenuPostCloseCallback<M> callback) {
        this.postCloseCallbacks.put(UUID.randomUUID().toString(), callback);
        return this;
    }

    /**
     * Add a callback that runs 1 tick after the inventory is closed, with access to {@link Player}.<br>
     * Note: This 1 tick delay is technically enough time for a player to log out. This event is guaranteed to run, but the player may not be online.<br>
     * This is the method you must use for Menus that you want to re-open when closed. Using {@link #addCloseCallback(String, MenuCloseCallback)} will cause recursion.
     * @param id The ID to associate with this callback for later removal
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addPostCloseCallback(@NotNull String id, @NotNull MenuPostCloseCallback<M> callback) {
        this.postCloseCallbacks.put(id, callback);
        return this;
    }

    /**
     * Remove a post-close callback by ID
     */
    public boolean removePostCloseCallback(@NotNull String id) {
        return this.postCloseCallbacks.remove(id) != null;
    }

    /**
     * Add a callback that runs when the inventory is opened.
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addOpenCallback(@Nullable MenuOpenCallback menuOpen) {
        this.openCallbacks.put(UUID.randomUUID().toString(), menuOpen);
        return this;
    }

    /**
     * Add a callback that runs when the inventory is opened.
     * @param id The ID to associate with this callback for later removal
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addOpenCallback(@NotNull String id, @Nullable MenuOpenCallback menuOpen) {
        if (menuOpen != null) {
            this.openCallbacks.put(id, menuOpen);
        }
        return this;
    }

    /**
     * Remove an open callback by ID
     */
    public boolean removeOpenCallback(@NotNull String id) {
        return this.openCallbacks.remove(id) != null;
    }

    /**
     * Listen to a player inventory click. (at a specific slot)
     * @param slot The player inventory slot number to listen to.
     * @return this {@link MenuEvents} object for chaining.
     */
    @NotNull
    public MenuEvents<M> addPlayerSlotClick(int slot, @NotNull PlayerSlotClick<M> click) {
        this.playerSlotClicks.computeIfAbsent(slot, k -> new HashMap<>()).put(UUID.randomUUID().toString(), click);
        return this;
    }

    /**
     * Listen to a player inventory click. (at a specific slot)
     * @param slot The player inventory slot number to listen to.
     * @param id The ID to associate with this click handler for later removal
     * @return this {@link MenuEvents} object for chaining.
     */
    @NotNull
    public MenuEvents<M> addPlayerSlotClick(int slot, @NotNull String id, @NotNull PlayerSlotClick<M> click) {
        this.playerSlotClicks.computeIfAbsent(slot, k -> new HashMap<>()).put(id, click);
        return this;
    }

    /**
     * Remove a player inventory click by ID
     */
    public boolean removePlayerSlotClick(@NotNull String id) {
        boolean removed = false;
        for (Map<String, PlayerSlotClick<M>> map : this.playerSlotClicks.values()) {
            if (map.remove(id) != null) {
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Adds a click event for a player slot.
     * @deprecated Use {@link #addPlayerInvClick(PlayerSlotClick)} instead.
     * This method will be removed in a future release.
     * @param click The {@link PlayerSlotClick} event to add.
     * @return This {@link MenuEvents} instance.
     */
    @NotNull
    @Deprecated
    public MenuEvents<M> addPlayerSlotClick(@NotNull PlayerSlotClick<M> click) {
        this.playerInvClicks.put(UUID.randomUUID().toString(), click);
        return this;
    }

    /**
     * Listen to ALL player inventory clicks. (in any slot)
     * @param click The callback to run when a player clicks a slot in their inventory.
     * @return this {@link MenuEvents} object for chaining.
     */
    public MenuEvents<M> addPlayerInvClick(@NotNull PlayerSlotClick<M> click) {
        this.playerInvClicks.put(UUID.randomUUID().toString(), click);
        return this;
    }

    /**
     * Listen to ALL player inventory clicks. (in any slot)
     * @param id The ID to associate with this click handler for later removal
     * @param click The callback to run when a player clicks a slot in their inventory.
     * @return this {@link MenuEvents} object for chaining.
     */
    public MenuEvents<M> addPlayerInvClick(@NotNull String id, @NotNull PlayerSlotClick<M> click) {
        this.playerInvClicks.put(id, click);
        return this;
    }

    /**
     * Remove a player inventory click by ID
     */
    public boolean removePlayerInvClick(@NotNull String id) {
        return this.playerInvClicks.remove(id) != null;
    }

    /**
     * Add a predicate on {@link InventoryClickEvent} that must pass for the click handlers on that slot to be executed.<br>
     * This applies to the {@link Player}'s inventory only, not the menu's inventory.<br>
     * For adding a predicate for the Menu inventory clicks, use {@link #addClickPredicate(Predicate)}
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addPlayerClickPredicate(@NotNull Predicate<InventoryClickEvent> predicate) {
        this.playerInvClickPredicates.put(UUID.randomUUID().toString(), predicate);
        return this;
    }

    /**
     * Add a predicate on {@link InventoryClickEvent} that must pass for the click handlers on that slot to be executed.<br>
     * This applies to the {@link Player}'s inventory only, not the menu's inventory.<br>
     * For adding a predicate for the Menu inventory clicks, use {@link #addClickPredicate(String, Predicate)}
     * @param id The ID to associate with this predicate for later removal
     * @return this {@link MenuEvents} object for chaining
     */
    @NotNull
    public MenuEvents<M> addPlayerClickPredicate(@NotNull String id, @NotNull Predicate<InventoryClickEvent> predicate) {
        this.playerInvClickPredicates.put(id, predicate);
        return this;
    }

    /**
     * Remove a player inventory click predicate by ID
     */
    public boolean removePlayerClickPredicate(@NotNull String id) {
        return this.playerInvClickPredicates.remove(id) != null;
    }

    @NotNull
    public MenuEvents<M> copy() {
        return new MenuEvents<>(this);
    }
}
