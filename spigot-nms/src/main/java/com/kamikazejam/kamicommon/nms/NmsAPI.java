package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractHoverEvent;
import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.provider.*;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.awt.Color;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NmsAPI {
    // ---------------------------------------------------------------------------------- //
    //                                    PROVIDERS                                       //
    // ---------------------------------------------------------------------------------- //
    private static final ChatColorProvider chatColorProvider = new ChatColorProvider();
    private static final BlockUtilProvider blockUtilProvider = new BlockUtilProvider();
    private static final HoverEventProvider hoverEventProvider = new HoverEventProvider();
    private static final ItemTextProvider itemTextProvider = new ItemTextProvider();
    private static final TeleportProvider teleportProvider = new TeleportProvider();
    private static final MainHandProvider mainHandProvider = new MainHandProvider();



    // ---------------------------------------------------------------------------------- //
    //                                   API METHODS                                      //
    // ---------------------------------------------------------------------------------- //
    public static Color getJavaColor(ChatColor chatColor) {
        return chatColorProvider.get().getColor(chatColor);
    }
    public static AbstractBlockUtil getBlockUtil() { return blockUtilProvider.get(); }
    public static AbstractHoverEvent getHoverEvent() { return hoverEventProvider.get(); }
    public static AbstractItemText getItemText() { return itemTextProvider.get(); }
    public static AbstractTeleporter getTeleporter() { return teleportProvider.get(); }
    public static @Nullable ItemStack getItemInMainHand(Player player) {
        return mainHandProvider.get().getItemInMainHand(player);
    }
}
