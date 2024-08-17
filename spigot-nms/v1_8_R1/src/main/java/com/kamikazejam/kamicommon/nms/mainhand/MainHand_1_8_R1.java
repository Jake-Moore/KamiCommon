package com.kamikazejam.kamicommon.nms.mainhand;

import com.kamikazejam.kamicommon.nms.abstraction.mainhand.AbstractMainHand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MainHand_1_8_R1 extends AbstractMainHand {
    @Override
    public @Nullable ItemStack getItemInMainHand(@NotNull PlayerInventory inventory) {
        return inventory.getItemInHand();
    }

    @Override
    public boolean isOffHand(@NotNull PlayerInteractEntityEvent event) {
        // No offhand in 1.8
        return false;
    }

    @Override
    public void setItemInMainHand(@NotNull Player player, @Nullable ItemStack itemStack) {
        player.setItemInHand(itemStack);
    }

    @Override
    public @Nullable ItemStack getItemInOffHand(@NotNull PlayerInventory playerInventory) {
        return null;
    }
}
