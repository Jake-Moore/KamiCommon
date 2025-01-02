package com.kamikazejam.kamicommon.menu;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.paginated.IPaginatedClickTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.simple.ISimpleClickTransform;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.util.ItemUtil;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    final Set<UpdatingMenu> autoUpdateInventories = Sets.newCopyOnWriteArraySet();

    // ------------------------------------------------------- //
    // --------------------- LISTENERS ----------------------- //
    // ------------------------------------------------------- //

    @EventHandler
    public void onClickMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) { return; }
        if (!(e.getInventory().getHolder() instanceof Menu menu)) { return; }

        // Handle player inventory clicks
        // If this method returns true, it means it has handled the event and we should not do anything else
        if (handlePlayerInventoryClick(e, menu, player)) { return; }

        // It wasn't a click in the player inventory, so let's assume it's in the menu inventory and enforce the menu rules
        if (menu.getOptions().isCancelClickEvent()) {
            e.setCancelled(true);
        }

        // test the predicates before the icon click handlers
        MenuEvents menuEvents = menu.getEvents();
        for (Predicate<InventoryClickEvent> predicate : menuEvents.getClickPredicates()) {
            if (!predicate.test(e)) {
                return;
            }
        }

        ItemStack current = e.getCurrentItem();
        if (current == null) { return; }

        // Fetch the MenuIcon that should be in our slot
        IMenuIconsAccess iconsAccess = menu.getMenuIconsAccess();
        @Nullable MenuIcon iconForSlot = iconsAccess.getMenuIcon(e.getSlot()).orElseGet(() -> {
           // Attempt to use the filler icon if that is enabled and nonnull
            if (menu.getOptions().getExcludedFillSlots().contains(e.getSlot())) { return null; }
            @Nullable MenuIcon fillerIcon = menu.getFillerIcon();
            if (fillerIcon == null || !fillerIcon.isEnabled()) { return null; }
            return fillerIcon;
        });
        @Nullable IClickTransform click = iconForSlot != null ? iconForSlot.getTransform() : null;

        // If there is no icon in the slot (or no click), we don't need to do anything
        if (iconForSlot == null || click == null) { return; }

        // Sanity check - ensure the ItemStack in the slot matches the icon
        if (!ItemUtil.isSimplySimilar(current, iconForSlot.getLastItem())) { return; }

        // Play the click sound
        iconForSlot.playClickSound(player);

        // Perform the Click
        if (click instanceof ISimpleClickTransform simpleClickTransform) {
            simpleClickTransform.process(player, e);
        }else if (click instanceof IPaginatedClickTransform paginatedClickTransform) {
            paginatedClickTransform.process(player, e, getPage(menu));
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();
        if (!(e.getInventory().getHolder() instanceof Menu menu)) {
            return;
        }
        MenuEvents menuEvents = menu.getEvents();
        if (menuEvents.getIgnoreNextInventoryCloseEvent().get()) { return; }

        // Remove this menu from the auto update list
        // We do this before consumers, because some consumers may re-open the menu
        if (e.getInventory().getHolder() instanceof UpdatingMenu updatingMenu) {
            autoUpdateInventories.remove(updatingMenu);
        }

        // Trigger the Close Consumers
        menuEvents.getCloseCallbacks().forEach(callback -> callback.onClose(p, e));

        // Trigger the Post-Close Consumers (1-tick later)
        final Menu finalMenu = menu;
        Bukkit.getScheduler().runTaskLater(SpigotUtilsSource.get(), () ->
                        menuEvents.getPostCloseCallbacks().forEach(callback -> callback.onPostClose(p, finalMenu))
                , 1L);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (e.getPlayer().getOpenInventory() == null || e.getPlayer().getOpenInventory().getTopInventory() == null) { return; }

        Inventory topInventory = e.getPlayer().getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof Menu menu)) { return; }

        if (!menu.getOptions().isAllowItemPickup()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getOpenInventory() == null || e.getPlayer().getOpenInventory().getTopInventory() == null) { return; }

        Inventory topInventory = e.getPlayer().getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof Menu menu)) { return; }

        if (!menu.getOptions().isAllowItemDrop()) {
            e.setCancelled(true);
        }
    }

    /**
     * @return The current page (0-indexed)
     */
    private int getPage(@NotNull Menu menu) {
        if (!(menu instanceof PaginatedMenu paginatedMenu)) { return 0; }
        return paginatedMenu.getCurrentPage();
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
            if (inv.getInventory().getViewers().isEmpty()) { continue; }
            inv.updateOneTick(); // Trigger that another tick has passed and we must update
            updated.add(inv);
        }

        // Send updates to all players affected by modified menus
        updated.forEach((inv) -> {
            for (HumanEntity entity : inv.getInventory().getViewers()) {
                if (!(entity instanceof Player p)) { continue; }
                p.updateInventory();
            }
        });
    }

    /**
     * @return true IFF the click was a player inventory click and it was handled by this method
     */
    private boolean handlePlayerInventoryClick(@NotNull InventoryClickEvent e, @NotNull Menu menu, @NotNull Player player) {
        final MenuEvents menuEvents = menu.getEvents();

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
        for (Predicate<InventoryClickEvent> predicate : menuEvents.getPlayerInvClickPredicates()) {
            if (!predicate.test(e)) {
                return true;
            }
        }

        // Assume standard 36 slot core inventory container
        // Note: they have an inventory open, they should not be able to click
        //  armor or offhand slots, if we receive that event, we should ignore it
        if (slot < 0 || slot > 35) { return true; }

        // If we have a generic slot listener -> call it
        menuEvents.getPlayerInvClicks().forEach((click) -> click.onClick(player, e.getClick(), slot));

        // If we have a specific-slot listener -> call it (lower priority)
        menuEvents.getPlayerSlotClicks().getOrDefault(slot, new ArrayList<>()).forEach(
                (click) -> click.onClick(player, e.getClick(), slot)
        );

        // Return (we handled the player click, and are done)
        return true;
    }
}