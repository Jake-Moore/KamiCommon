package com.kamikazejamplugins.kamicommon.gui;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.gui.interfaces.Menu;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickInfo;
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

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class MenuManager implements Listener {

    @EventHandler
    public void onClickMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player member = (Player) e.getWhoClicked();
        if (member == null) {
            return;
        }

        if (!(e.getInventory().getHolder() instanceof Menu)) {
            return;
        }
        e.setCancelled(true);

        Menu<Player> menu = (Menu<Player>) e.getInventory().getHolder();

        // handle the click handler before the item click handlers
        Predicate<InventoryClickEvent> consumer = menu.getClickHandler();
        if (consumer != null) {
            if (!consumer.test(e)) {
                return;
            }
        }

        ItemStack current = e.getCurrentItem();
        if (current != null) {
            for (Map.Entry<MenuItem, MenuClickInfo<Player>> entry : menu.getClickableItems().entrySet()) {
                if (entry != null) {
                    MenuClickInfo<Player> click = entry.getValue();
                    if (click != null) {
                        MenuItem menuItem = entry.getKey();
                        if (menuItem != null) {
                            boolean sameItems = compareItemStacks(current, menuItem.getItem());
                            if (menuItem.getSlot() == e.getSlot() && sameItems && e.getClickedInventory().getType() != InventoryType.PLAYER) {
                                click.onItemClickMember(member, e);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (!(e.getInventory().getHolder() instanceof Menu)) {
            return;
        }

        Menu<?> menu = (Menu<?>) e.getInventory().getHolder();

        Consumer<InventoryCloseEvent> close = menu.getCloseHandler();
        if (close != null) {
            if (menu.getIgnoredClose().contains(p.getName())) {
                menu.getIgnoredClose().remove(p.getName());
            } else {
                Bukkit.getScheduler().runTaskLater(KamiCommon.getPlugin(), () -> close.accept(e), 1);
            }
        }

        Consumer<InventoryCloseEvent> instantClose = menu.getInstantCloseHandler();
        if (instantClose != null) {
            instantClose.accept(e);
        }

        MenuTask.getAutoUpdateInventories().remove(menu);
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if(e.getPlayer().getOpenInventory() == null) { return; }
        if (!(e.getPlayer().getInventory().getHolder() instanceof Menu)) { return; }

        Menu<Player> menu = (Menu<Player>) e.getPlayer().getInventory().getHolder();
        if(!menu.allowItemPickup()) {
            e.setCancelled(true);
        }
    }

    public static boolean compareItemStacks(ItemStack item1, ItemStack item2) {
        //Check null, material types, and amounts
        if (item1 == null || item2 == null) { return false; }
        if (item1.getType() != item2.getType()) { return false; }
        if (item1.getAmount() != item2.getAmount()) { return false; }

        if (XMaterial.matchXMaterial(item1).equals(XMaterial.POTION) && XMaterial.matchXMaterial(item2).equals(XMaterial.POTION) || item1.getDurability() == item2.getDurability()) {
            ItemMeta meta1 = item1.getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();

            //Compare leather colors
            if (meta1 instanceof LeatherArmorMeta && meta2 instanceof LeatherArmorMeta && !((LeatherArmorMeta) meta1).getColor().equals(((LeatherArmorMeta) meta2).getColor())) { return false; }

            //Compare display names
            if (!meta1.hasDisplayName() || !meta2.hasDisplayName() || !meta1.getDisplayName().equals(meta2.getDisplayName())) {
                if (meta1.hasDisplayName() && meta2.hasDisplayName()) { return false; }
            }

            //Compare lores
            if (!meta1.hasLore() || !meta2.hasLore() || !meta1.getLore().equals(meta2.getLore())) {
                if (meta1.hasLore() && meta2.hasLore()) { return false; }
            }

            return item1.getEnchantments().equals(item2.getEnchantments());
        }
        return false;
    }

}