package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractHoverEvent;
import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.provider.*;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NmsAPI {
    // ---------------------------------------------------------------------------------- //
    //                                    PROVIDERS                                       //
    // ---------------------------------------------------------------------------------- //
    @Getter private static final ChatColorProvider chatColorProvider = new ChatColorProvider();
    @Getter private static final BlockUtilProvider blockUtilProvider = new BlockUtilProvider();
    @Getter private static final HoverEventProvider hoverEventProvider = new HoverEventProvider();
    @Getter private static final ItemTextProvider itemTextProvider = new ItemTextProvider();
    @Getter private static final TeleportProvider teleportProvider = new TeleportProvider();
    @Getter private static final MainHandProvider mainHandProvider = new MainHandProvider();
    @Getter private static final EnchantIDProvider enchantIDProvider = new EnchantIDProvider();



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

    public static String getNamespaced(Enchantment enchantment) {
        return enchantIDProvider.get().getNamespaced(enchantment);
    }
}
