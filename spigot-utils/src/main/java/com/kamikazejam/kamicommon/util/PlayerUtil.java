package com.kamikazejam.kamicommon.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && XMaterial.matchXMaterial(item) != XMaterial.AIR) {
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

    /**
     * Will try to take the items from the player's inventory
     * @param enforceQuantities If we should fail if we cannot take all the items
     * @return True IFF all items were taken
     */
    public static boolean takeItems(Player player, boolean enforceQuantities, ItemStack... itemStacks) {
        if (enforceQuantities && !canTakeItems(player, itemStacks)) {
            return false;
        }
        for (ItemStack itemStack : itemStacks) {
            takeItem(player, itemStack);
        }
        return true;
    }

    private static void takeItem(Player player, ItemStack itemStack) {
        int amount = itemStack.getAmount();

        // Loop through the inventory removing/subtracting amounts until we hit quota
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() < 1) { continue; }
            if (!stack.isSimilar(itemStack)) { continue; }

            int stackAmount = stack.getAmount();
            if (stackAmount > amount) {
                stack.setAmount(stackAmount - amount);
                return;
            } else if (stackAmount == amount) {
                player.getInventory().remove(stack);
                return;
            } else {
                amount -= stackAmount;
                player.getInventory().remove(stack);
            }
        }
    }

    public static boolean canTakeItems(Player player, ItemStack... itemStacks) {
        // Create a map of Items to take, and their quantities
        Map<ItemStack, Integer> takeAmounts = new HashMap<>();
        for (ItemStack itemStack : itemStacks) {
            ItemStack clone = itemStack.clone(); clone.setAmount(1);

            int amount = takeAmounts.getOrDefault(clone, 0);
            takeAmounts.put(clone, amount + itemStack.getAmount());
        }

        // Create a map of Player's items, and their quantities
        Map<ItemStack, Integer> playerAmounts = new HashMap<>();
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() < 1) { continue; }
            ItemStack clone = stack.clone(); clone.setAmount(1);

            int amount = playerAmounts.getOrDefault(clone, 0);
            playerAmounts.put(clone, amount + stack.getAmount());
        }

        // Check if the player has enough of each item
        for (Map.Entry<ItemStack, Integer> entry : takeAmounts.entrySet()) {
            ItemStack itemStack = entry.getKey();
            int amount = entry.getValue();

            int playerAmount = playerAmounts.getOrDefault(itemStack, 0);
            if (playerAmount < amount) { return false; }
        }
        return true;
    }

    /**
     * @return true IFF (player != null AND player.isOnline() AND player.isValid())
     */
    public static boolean isFullyValidPlayer(@Nullable Player player) {
        return player != null && player.isOnline() && player.isValid();
    }
}
