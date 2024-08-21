package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.gui.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MenuManager implements Listener {

    @EventHandler
    public void onClickMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) { return; }
        if (!(e.getInventory().getHolder() instanceof KamiMenu menu)) { return; }
        e.setCancelled(true);

        // Special Handling for clicks in the player inventory
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) {
            // For now just ignore it -> return
            // TODO add player inventory click handling
            return;
        }

        // test the click predicate before the item click handlers
        @Nullable Predicate<InventoryClickEvent> consumer = menu.getClickPredicate();
        if (consumer != null && !consumer.test(e)) {
            return;
        }

        ItemStack current = e.getCurrentItem();
        if (current == null) { return; }

        int page = (menu.getParent() != null) ? menu.getParent().getCurrentPage() : 0;

        for (MenuItem tickedItem : menu.getMenuItems()) {
            if (tickedItem == null) { continue; }

            IClickTransform click = tickedItem.getTransform();
            if (click == null) { continue; }

            @Nullable ItemSlot itemSlot = tickedItem.getItemSlot();
            if (itemSlot == null) { continue; }

            // We use the cached copy from when it was added to the inventory
            // Since it may change through its lifecycle
            boolean sameItems = compareItemStacks(current, tickedItem.getLastItem());
            if (sameItems && itemSlot.get(menu).contains(e.getSlot())) {
                click.process(player, e, page);
                return;
            }
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (!(e.getInventory().getHolder() instanceof KamiMenu menu)) {
            return;
        }

        @Nullable Consumer<Player> close = menu.getPostCloseConsumer();
        if (close != null) {
            if (menu.getIgnoredClose().contains(p.getName())) {
                menu.getIgnoredClose().remove(p.getName());
            } else {
                Bukkit.getScheduler().runTaskLater(PluginSource.get(), () -> close.accept(p), 1);
            }
        }

        @Nullable Consumer<InventoryCloseEvent> preClose = menu.getPreCloseConsumer();
        if (preClose != null) {
            preClose.accept(e);
        }

        MenuTask.getAutoUpdateInventories().remove(menu);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if(e.getPlayer().getOpenInventory() == null) { return; }
        if (!(e.getPlayer().getInventory().getHolder() instanceof KamiMenu menu)) { return; }

        if (!menu.isAllowItemPickup()) {
            e.setCancelled(true);
        }
    }

    public static boolean compareItemStacks(ItemStack item1, ItemStack item2) {
        //Check null, material types, and amounts
        if (item1 == null || item2 == null) { return false; }
        if (item1.getType() != item2.getType()) { return false; }
        if (item1.getAmount() != item2.getAmount()) { return false; }
        if (item1.hasItemMeta() != item2.hasItemMeta()) { return false; }

        if (XMaterial.matchXMaterial(item1).equals(XMaterial.POTION) && XMaterial.matchXMaterial(item2).equals(XMaterial.POTION) || item1.getDurability() == item2.getDurability()) {
            ItemMeta meta1 = item1.getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();

            // Higher versions can have null metas
            if (meta1 != null && meta2 != null) {
                //Compare leather colors
                if (meta1 instanceof LeatherArmorMeta && meta2 instanceof LeatherArmorMeta && !((LeatherArmorMeta) meta1).getColor().equals(((LeatherArmorMeta) meta2).getColor())) {
                    return false;
                }

                //Compare display names
                if (!meta1.hasDisplayName() || !meta2.hasDisplayName() || !meta1.getDisplayName().equals(meta2.getDisplayName())) {
                    if (meta1.hasDisplayName() && meta2.hasDisplayName()) { return false; }
                }

                //Compare lore
                if (!meta1.hasLore() || !meta2.hasLore() || !Objects.equals(meta1.getLore(), meta2.getLore())) {
                    if (meta1.hasLore() && meta2.hasLore()) { return false; }
                }
            }

            return item1.getEnchantments().equals(item2.getEnchantments());
        }
        return false;
    }

}