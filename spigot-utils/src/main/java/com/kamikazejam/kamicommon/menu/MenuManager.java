package com.kamikazejam.kamicommon.menu;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.menu.items.MenuItem;
import com.kamikazejam.kamicommon.menu.items.slots.ItemSlot;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.kamikazejam.kamicommon.menu.OLD_PAGED_KAMI_MENU.META_DATA_KEY;

@Getter
public class MenuManager implements Listener, Runnable {
    // TODO with multiple types of menu classes, we need to abstract out the auto updating fields and methods into a common interface
    protected final Set<OLD_KAMI_MENU> autoUpdateInventories = Sets.newCopyOnWriteArraySet();

    // ------------------------------------------------------- //
    // --------------------- LISTENERS ----------------------- //
    // ------------------------------------------------------- //

    @EventHandler
    public void onClickMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) { return; }
        if (!(e.getInventory().getHolder() instanceof OLD_KAMI_MENU menu)) { return; }

        if (menu.isCancelOnClick()) {
            e.setCancelled(true);
        }

        // Special Handling for clicks in the player inventory
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) {
            // Get the player inventory slot that was clicked. This should be in range [0, 35]
            // Where 0-8 are the hotbar slots from left to right.
            // Then slot 9 starts at the top left of the player inventory, and goes right and down to 35.
            int slot = e.getSlot();

            // Assume standard 36 slot core inventory container
            // Note: they have an inventory open, they should not be able to click
            //  armor or offhand slots, if we receive that event, we should ignore it
            if (slot < 0 || slot > 35) { return; }

            // If we have a generic slot listener -> call it
            menu.getPlayerInvClicks().forEach((click) -> click.onClick(player, e.getClick(), slot));

            // If we have a specific-slot listener -> call it (lower priority)
            menu.getPlayerSlotClicks().getOrDefault(slot, new ArrayList<>()).forEach(
                    (click) -> click.onClick(player, e.getClick(), slot)
            );

            // Return (we handled the player click, and are done)
            return;
        }

        // test the click predicate before the item click handlers
        for (Predicate<InventoryClickEvent> predicate : menu.getClickPredicates()) {
            if (!predicate.test(e)) {
                return;
            }
        }

        ItemStack current = e.getCurrentItem();
        if (current == null) { return; }

        int page = getPage(menu);
        for (MenuItem menuItem : menu.getMenuItems().values()) {
            if (menuItem == null) { continue; }

            IClickTransform click = menuItem.getTransform();
            if (click == null) { continue; }

            @Nullable ItemSlot itemSlot = menuItem.getItemSlot();
            if (itemSlot == null || !itemSlot.get(menu).contains(e.getSlot())) { continue; }

            // We use the cached copy from when it was added to the inventory
            // Since it may change through its lifecycle
            if (ItemUtil.isSimplySimilar(current, menuItem.getLastItem())) {
                menuItem.playClickSound(player);
                click.process(player, e, page);
                return;
            }
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();

        if (!(e.getInventory().getHolder() instanceof OLD_KAMI_MENU menu)) {
            return;
        }

        // Remove this menu from the auto update list
        // We do this before consumers, because some consumers may re-open the menu
        autoUpdateInventories.remove(menu);

        // Trigger the Close Consumers
        menu.getCloseConsumers().forEach(consumer -> consumer.accept(e));

        // Trigger the Post-Close Consumers (1-tick later)
        Bukkit.getScheduler().runTaskLater(SpigotUtilsSource.get(), () ->
                menu.getPostCloseConsumers().forEach(consumer -> consumer.accept(p))
        , 1L);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if(e.getPlayer().getOpenInventory() == null) { return; }
        if (!(e.getPlayer().getInventory().getHolder() instanceof OLD_KAMI_MENU menu)) { return; }

        if (!menu.isAllowItemPickup()) {
            e.setCancelled(true);
        }
    }

    // TODO REMOVE AND USE DEDICATED PAGINATED MENU CLASS
    private int getPage(@NotNull OLD_KAMI_MENU menu) {
        Object o = menu.getMetaData().get(META_DATA_KEY);
        if (!(o instanceof OLD_PAGED_KAMI_MENU paged)) { return 0; }
        return paged.getCurrentPage();
    }



    // ------------------------------------------------------- //
    // ---------------------- RUNNABLE ----------------------- //
    // ------------------------------------------------------- //
    /**
     * This task is called every tick in a task registered by {@link SpigotUtilsSource}
     */
    @Override
    public void run() {
        Set<OLD_KAMI_MENU> updated = new HashSet<>();

        // Check and run any sub-tasks for each inventory
        for (OLD_KAMI_MENU inv : autoUpdateInventories) {
            if (inv.getInventory().getViewers().isEmpty()) { continue; }
            inv.getTickCounter().getAndIncrement();
            // Trigger dynamic item updates on this menu
            inv.update();
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