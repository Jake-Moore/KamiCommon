package com.kamikazejamplugins.kamicommon.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Utility class for helping with players
 * You can give items (which drop near them if they are full), and clean their inventory (with or without armor)
 */
@SuppressWarnings("unused")
public class PlayerUtil {
    /**
     * If the player's inventory is empty (Includes armor slots in the check)
     * @param player The player to check
     * @return If the inventory was empty
     */
    public static boolean emptyInventory(Player player) {
        return emptyInventory(player, true);
    }

    /**
     * If the player's inventory is empty
     * @param player The player to check
     * @param checkArmor If the armor slots should be included in the check
     * @return If the inventory was empty
     */
    public static boolean emptyInventory(Player player, boolean checkArmor) {
        //Check their 36 slots for items
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getItem(i) != null && XMaterial.matchXMaterial(player.getInventory().getItem(i)) != XMaterial.AIR) {
                return false;
            }
        }

        //Check their amor slots for items
        if (checkArmor) {
            for (ItemStack i : player.getInventory().getArmorContents()) {
                if (i != null && XMaterial.matchXMaterial(i) != XMaterial.AIR) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void giveItems(Player player, ItemStack... itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            giveItem(player, itemStack);
        }
    }

    public static void giveItem(Player player, ItemStack itemStack) {
        ItemStack leftOver = stackItem(player, itemStack);
        if (leftOver != null) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), leftOver);
            } else {
                player.getInventory().addItem(leftOver);
            }
        }
    }

    public static @Nullable ItemStack stackItem(Player player, ItemStack itemStack) {
        for (int i = 0; i < 36; i++) {
            ItemStack invItem = player.getInventory().getItem(i);
            if (invItem == null) { continue; }
            if (invItem.isSimilar(itemStack) && invItem.getAmount() < invItem.getMaxStackSize()) {
                int amount = itemStack.getAmount();
                if (invItem.getAmount() + amount > invItem.getMaxStackSize()) {
                    int leftOver = (invItem.getAmount() + amount) - invItem.getMaxStackSize();
                    invItem.setAmount(invItem.getMaxStackSize());

                    // Attempt to stack the left over
                    ItemStack clone = itemStack.clone();
                    clone.setAmount(leftOver);
                    return stackItem(player, clone);
                } else {
                    invItem.setAmount(invItem.getAmount() + amount);
                    return null;
                }
            }
        }
        return itemStack;
    }

}
