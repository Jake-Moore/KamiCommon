package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
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
    @Getter private static final ItemTextProviderPre_1_17 itemTextProviderPre_1_17 = new ItemTextProviderPre_1_17();
    @Getter private static final TeleportProvider teleportProvider = new TeleportProvider();
    @Getter private static final MainHandProvider mainHandProvider = new MainHandProvider();
    @Getter private static final EnchantIDProvider enchantIDProvider = new EnchantIDProvider();
    @Getter private static final MessageManagerProvider messageManagerProvider = new MessageManagerProvider();
    @Getter private static final ItemEditorProvider itemEditorProvider = new ItemEditorProvider();
    @Getter private static final NmsItemProvider nmsItemProvider = new NmsItemProvider();



    // ---------------------------------------------------------------------------------- //
    //                                   API METHODS                                      //
    // ---------------------------------------------------------------------------------- //
    public static Color getJavaColor(ChatColor chatColor) {
        return chatColorProvider.get().getColor(chatColor);
    }
    public static AbstractBlockUtil getBlockUtil() { return blockUtilProvider.get(); }
    public static AbstractItemTextPre_1_17 getItemText() { return itemTextProviderPre_1_17.get(); }
    public static AbstractTeleporter getTeleporter() { return teleportProvider.get(); }
    public static AbstractMessageManager getMessageManager() { return messageManagerProvider.get(); }
    public static AbstractItemEditor getItemEditor() { return itemEditorProvider.get(); }
    public static NmsItemMethods getNmsItemMethods() { return nmsItemProvider.get(); }

    public static @Nullable ItemStack getItemInMainHand(Player player) {
        return mainHandProvider.get().getItemInMainHand(player);
    }
    public static void setItemInMainHand(Player player, @Nullable ItemStack itemStack) {
        mainHandProvider.get().setItemInMainHand(player, itemStack);
    }

    public static String getNamespaced(Enchantment enchantment) {
        return enchantIDProvider.get().getNamespaced(enchantment);
    }
}
