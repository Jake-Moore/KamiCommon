package com.kamikazejam.kamicommon.menu;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.api.clicks.data.MenuClickData;
import com.kamikazejam.kamicommon.menu.api.clicks.data.PlayerClickData;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.MenuClickTransform;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.util.ItemUtil;
import com.kamikazejam.kamicommon.util.data.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This manager is responsible for handling all {@link Menu} interactions.<br>
 * Developers should not need to interact with this class directly, only through specific menu classes
 * like {@link SimpleMenu} or {@link PaginatedMenu}.
 */
@Getter
public final class MenuManager implements Listener, Runnable {
    @Internal
    final Set<UpdatingMenu> autoUpdateInventories = Sets.newCopyOnWriteArraySet();

    // ------------------------------------------------------- //
    // --------------------- LISTENERS ----------------------- //
    // ------------------------------------------------------- //

    @SuppressWarnings({"rawtypes", "unchecked"})
    @EventHandler
    public void onClickMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {return;}
        if (!(e.getInventory().getHolder() instanceof Menu menu)) {return;}

        // Handle player inventory clicks
        // If this method returns true, it means it has handled the event and we should not do anything else
        if (handlePlayerInventoryClick(e, menu, player)) {return;}

        // Handle click event cancellation (now that we know it is a menu click)
        if (menu.getOptions().isCancelClickEvent()) {
            e.setCancelled(true);
        }

        // test the predicates before the icon click handlers
        MenuEvents<?> menuEvents = menu.getEvents();
        for (Predicate<InventoryClickEvent> predicate : menuEvents.getClickPredicates().values()) {
            if (!predicate.test(e)) {
                return;
            }
        }

        // Special Handling of OneClickMenu
        if (menu instanceof OneClickMenu oneClickMenu) {
            processOneClick(e, player, oneClickMenu);
            return;
        }

        // Process normal menu clicks
        processClick(e, player, menu);
    }

    private static void processOneClick(InventoryClickEvent e, Player player, OneClickMenu oneClickMenu) {
        if (oneClickMenu.clicked) {
            // OneClickMenu has already been clicked -> cancel all future clicks
            e.setCancelled(true);
            return;
        }

        // Process the 'one' click
        @Nullable MenuIcon<OneClickMenu> icon = getMenuIcon(e, oneClickMenu, oneClickMenu.getMenuIconsAccess());
        if (icon == null) {
            // Icon validation failed, do not process the click
            return;
        }

        // Ignore clicks that do not count for the one-click
        if (!oneClickMenu.countsForClick(icon, e.getSlot())) {
            return;
        }

        // Allow this click
        oneClickMenu.clicked = true;
        MenuClickData<OneClickMenu> data = oneClickMenu.buildClickData(
                oneClickMenu,
                player,
                e.getClick(),
                e,
                0,
                icon,
                e.getSlot()
        );
        oneClickMenu.getTransform().process(data);
    }

    private <M extends Menu<M>> void processClick(InventoryClickEvent e, Player player, M menu) {

        // Fetch the MenuIcon<M> that should be in our slot
        @Nullable Pair<MenuIcon<M>, MenuClickTransform<M>> pair = validateSlotClick(e, menu, menu.getMenuIconsAccess());
        if (pair == null) {
            // Icon validation failed, do not process the click
            return;
        }

        // Perform the Click
        int page = getPage(menu);
        MenuClickData<M> data = menu.buildClickData(
                menu,
                player,
                e.getClick(),
                e,
                page,
                pair.getA(),
                e.getSlot()
        );
        pair.getB().process(data);
    }

    private static <M extends Menu<M>> @Nullable Pair<MenuIcon<M>, MenuClickTransform<M>> validateSlotClick(
            InventoryClickEvent e, M menu,
            IMenuIconsAccess<M> iconsAccess
    ) {

        // Fetch MenuIcon for the clicked slot
        @Nullable MenuIcon<M> iconForSlot = getMenuIcon(e, menu, iconsAccess);
        if (iconForSlot == null) {return null;} // If there is no icon in the slot, we don't need to do anything

        // Fetch the MenuClickTransform for the icon
        @Nullable MenuClickTransform<M> click = iconForSlot.getTransform();
        if (click == null) {
            // If there is no click transform, we don't need to do anything
            return null;
        }

        return Pair.of(iconForSlot, click);
    }

    private static <M extends Menu<M>> @Nullable MenuIcon<M> getMenuIcon(
            @NotNull InventoryClickEvent e,
            @NotNull M menu,
            @NotNull IMenuIconsAccess<M> iconsAccess
    ) {
        @Nullable ItemStack current = e.getCurrentItem();
        if (current == null) {return null;}

        @Nullable MenuIcon<M> icon = iconsAccess.getMenuIcon(e.getSlot()).orElseGet(() -> {
            // Attempt to use the filler icon if that is enabled and nonnull
            if (menu.getOptions().getExcludedFillSlots().contains(e.getSlot())) {return null;}
            @Nullable MenuIcon<M> fillerIcon = menu.getFillerIcon();
            if (fillerIcon == null || !fillerIcon.isEnabled()) {return null;}
            return fillerIcon;
        });
        if (icon == null) {
            return null;
        }

        // Security check - ensure the ItemStack in the slot matches the icon
        if (!ItemUtil.isSimplySimilar(current, icon.getLastItem())) {return null;}

        return icon;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();
        if (!(e.getInventory().getHolder() instanceof Menu menu)) {
            return;
        }

        processClose(e, menu, p);
    }

    private <M extends Menu<M>> void processClose(InventoryCloseEvent e, M menu, Player p) {
        MenuEvents<M> menuEvents = menu.getEvents();
        if (menuEvents.getIgnoreNextInventoryCloseEvent().get()) {return;}

        // Remove this menu from the auto update list
        // We do this before consumers, because some consumers may re-open the menu
        if (e.getInventory().getHolder() instanceof UpdatingMenu updatingMenu) {
            autoUpdateInventories.remove(updatingMenu);
        }

        // Trigger the Close Consumers
        menuEvents.getCloseCallbacks().values().forEach(callback -> callback.onClose(p, e));

        // Trigger the Post-Close Consumers (1-tick later)
        final M finalMenu = menu;
        Bukkit.getScheduler().runTaskLater(SpigotUtilsSource.get(), () ->
                        menuEvents.getPostCloseCallbacks().values().forEach(callback -> callback.onPostClose(p, finalMenu))
                , 1L);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (e.getPlayer().getOpenInventory() == null || e.getPlayer().getOpenInventory().getTopInventory() == null) {return;}

        Inventory topInventory = e.getPlayer().getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof Menu<?> menu)) {return;}

        if (!menu.getOptions().isAllowItemPickup()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getOpenInventory() == null || e.getPlayer().getOpenInventory().getTopInventory() == null) {return;}

        Inventory topInventory = e.getPlayer().getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof Menu<?> menu)) {return;}

        if (!menu.getOptions().isAllowItemDrop()) {
            e.setCancelled(true);
        }
    }


    // ------------------------------------------------------- //
    // ---------------------- RUNNABLE ----------------------- //
    // ------------------------------------------------------- //

    /**
     * This task is called every tick in a task registered by {@link SpigotUtilsSource}
     */
    @Override
    public void run() {
        Set<UpdatingMenu> updated = new HashSet<>();

        // Check and run any sub-tasks for each inventory
        for (UpdatingMenu inv : autoUpdateInventories) {
            if (inv.getInventory().getViewers().isEmpty()) {continue;}
            inv.updateOneTick(); // Trigger that another tick has passed and we must update
            updated.add(inv);
        }

        // Send updates to all players affected by modified menus
        updated.forEach((inv) -> {
            for (HumanEntity entity : inv.getInventory().getViewers()) {
                if (!(entity instanceof Player p)) {continue;}
                p.updateInventory();
            }
        });
    }

    /**
     * @return true IFF the click was a player inventory click and it was handled by this method
     */
    private <M extends Menu<M>> boolean handlePlayerInventoryClick(@NotNull InventoryClickEvent e, @NotNull M menu, @NotNull Player player) {
        final MenuEvents<M> menuEvents = menu.getEvents();

        if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.PLAYER) {
            return false;
        }

        // It is a click in the player inventory, so let's enforce the player inv rules
        if (menu.getOptions().isCancelPlayerClickEvent()) {
            e.setCancelled(true);
        }

        // Get the player inventory slot that was clicked. This should be in range [0, 35]
        // Where 0-8 are the hotbar slots from left to right.
        // Then slot 9 starts at the top left of the player inventory, and goes right and down to 35.
        int slot = e.getSlot();

        // test the predicates before the player click handlers
        for (Predicate<InventoryClickEvent> predicate : menuEvents.getPlayerInvClickPredicates().values()) {
            if (!predicate.test(e)) {
                return true;
            }
        }

        // Assume standard 36 slot core inventory container
        // Note: they have an inventory open, they should not be able to click
        //  armor or offhand slots, if we receive that event, we should ignore it
        if (slot < 0 || slot > 35) {return true;}

        // Construct the click data
        @NotNull PlayerClickData<M> clickData = menu.buildPlayerClickData(
                menu,
                player,
                e.getClick(),
                e,
                slot
        );

        // If we have a generic slot listener -> call it
        menuEvents.getPlayerInvClicks().values().forEach((click) -> click.onClick(clickData));

        // If we have a specific-slot listener -> call it (lower priority)
        menuEvents.getPlayerSlotClicks().getOrDefault(slot, new HashMap<>()).values().forEach(
                (click) -> click.onClick(clickData)
        );

        // Return (we handled the player click, and are done)
        return true;
    }

    /**
     * @return The current page (0-indexed)
     */
    private int getPage(@NotNull Menu<?> menu) {
        if (!(menu instanceof PaginatedMenu paginatedMenu)) {return 0;}
        return paginatedMenu.getCurrentPage();
    }
}