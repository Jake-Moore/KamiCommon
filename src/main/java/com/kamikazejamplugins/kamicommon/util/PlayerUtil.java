package com.kamikazejamplugins.kamicommon.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
}
