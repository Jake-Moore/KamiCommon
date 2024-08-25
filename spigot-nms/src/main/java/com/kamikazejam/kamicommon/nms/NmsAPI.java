package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.event.EventManager;
import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.provider.*;
import com.kamikazejam.kamicommon.nms.wrapper.NMSWorldWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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
    @Getter private static final EntityMethodsProvider entityMethodsProvider = new EntityMethodsProvider();
    @Getter private static final PacketHandlerProvider packetHandlerProvider = new PacketHandlerProvider();
    @Getter private static final EventManagerProvider eventManagerProvider = new EventManagerProvider();

    // ---------------------------------------------------------------------------------- //
    //                                     WRAPPERS                                       //
    // ---------------------------------------------------------------------------------- //
    @Getter private static final NMSWorldWrapper nmsWorldWrapper = new NMSWorldWrapper();

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
    public static AbstractEntityMethods getEntityMethods() { return entityMethodsProvider.get(); }
    public static NMSPacketHandler getPacketHandler() { return packetHandlerProvider.get(); }
    public static EventManager getEventManager() { return eventManagerProvider.get(); }

    public static @Nullable ItemStack getItemInMainHand(@NotNull Player player) {
        return mainHandProvider.get().getItemInMainHand(player);
    }
    public static void setItemInMainHand(@NotNull Player player, @Nullable ItemStack itemStack) {
        mainHandProvider.get().setItemInMainHand(player, itemStack);
    }
    public static @Nullable ItemStack getItemInOffHand(@NotNull Player player) {
        return mainHandProvider.get().getItemInOffHand(player);
    }

    public static String getNamespaced(Enchantment enchantment) {
        return enchantIDProvider.get().getNamespaced(enchantment);
    }
    public static @NotNull NMSWorld getNMSWorld(@NotNull World world) {
        return nmsWorldWrapper.get(world);
    }
}
