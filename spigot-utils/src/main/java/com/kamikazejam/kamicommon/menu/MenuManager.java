package com.kamikazejam.kamicommon.menu;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.paginated.IPaginatedClickTransform;
import com.kamikazejam.kamicommon.menu.api.clicks.transform.simple.ISimpleClickTransform;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.paginated.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.simple.SimpleMenu;
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

        MenuEvents menuEvents = menu.getEvents();

        // Special Handling for clicks in the player inventory
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) {
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
                    return;
                }
            }

            // Assume standard 36 slot core inventory container
            // Note: they have an inventory open, they should not be able to click
            //  armor or offhand slots, if we receive that event, we should ignore it
            if (slot < 0 || slot > 35) { return; }

            // If we have a generic slot listener -> call it
            menuEvents.getPlayerInvClicks().forEach((click) -> click.onClick(player, e.getClick(), slot));

            // If we have a specific-slot listener -> call it (lower priority)
            menuEvents.getPlayerSlotClicks().getOrDefault(slot, new ArrayList<>()).forEach(
                    (click) -> click.onClick(player, e.getClick(), slot)
            );

            // Return (we handled the player click, and are done)
            return;
        }

        // It wasn't a click in the player inventory, so let's assume it's in the menu inventory and enforce the menu rules
        if (menu.getOptions().isCancelClickEvent()) {
            e.setCancelled(true);
        }

        // test the predicates before the icon click handlers
        for (Predicate<InventoryClickEvent> predicate : menuEvents.getClickPredicates()) {
            if (!predicate.test(e)) {
                return;
            }
        }

        ItemStack current = e.getCurrentItem();
        if (current == null) { return; }

        for (MenuIcon menuIcon : menu.getMenuIcons().values()) {
            if (menuIcon == null) { continue; }

            IClickTransform click = menuIcon.getTransform();
            if (click == null) { continue; }

            // Skip the icon if the slot doesn't align
            @Nullable IconSlot iconSlot = menuIcon.getIconSlot();
            if ((iconSlot == null || !iconSlot.get(menu).contains(e.getSlot()))) { continue; }

            // We use the cached copy from when it was added to the inventory
            // Since it may change through its lifecycle
            if (!ItemUtil.isSimplySimilar(current, menuIcon.getLastItem())) { continue; }

            // Play the click sound
            menuIcon.playClickSound(player);

            //      Perform the Click
            // Handle SimpleMenu Clicks
            if (click instanceof ISimpleClickTransform simpleClickTransform) {
                simpleClickTransform.process(player, e);
            }else if (click instanceof IPaginatedClickTransform paginatedClickTransform) {
                paginatedClickTransform.process(player, e, getPage(menu));
            }
            return;
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();
        if (!(e.getInventory().getHolder() instanceof Menu menu)) {
            return;
        }
        MenuEvents menuEvents = menu.getEvents();

        // Remove this menu from the auto update list
        // We do this before consumers, because some consumers may re-open the menu
        if (e.getInventory().getHolder() instanceof UpdatingMenu updatingMenu) {
            autoUpdateInventories.remove(updatingMenu);
        }

        // Trigger the Close Consumers
        menuEvents.getCloseCallbacks().forEach(callback -> callback.onClose(p, e));

        // Trigger the Post-Close Consumers (1-tick later)
        Bukkit.getScheduler().runTaskLater(SpigotUtilsSource.get(), () ->
                        menuEvents.getPostCloseCallbacks().forEach(callback -> callback.onPostClose(p, menu))
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

    private int getPage(@NotNull Menu menu) {
        if (!(menu instanceof PaginatedMenu paginatedMenu)) { return 0; }
        return 0;
        // TODO return paginatedMenu.getCurrentPage();
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
}