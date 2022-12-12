package com.kamikazejamplugins.kamicommon.util;

import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class ActionBar {
    private final String text;
    public ActionBar(String text) {
        this.text = text;
    }

    public void sendToPlayer(Player p) {
        String version = NmsManager.getNMSVersion();
        switch(version) {
            case "v1_8_R1":
                net.minecraft.server.v1_8_R1.PacketPlayOutChat v1_8_R1Packet = new net.minecraft.server.v1_8_R1.PacketPlayOutChat(new net.minecraft.server.v1_8_R1.ChatComponentText(text), (byte) 2);
                ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_8_R1Packet);
                break;
            case "v1_8_R2":
                net.minecraft.server.v1_8_R2.PacketPlayOutChat v1_8_R2Packet = new net.minecraft.server.v1_8_R2.PacketPlayOutChat(net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
                ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_8_R2Packet);
                break;
            case "v1_8_R3":
                net.minecraft.server.v1_8_R3.PacketPlayOutChat v1_8_R3Packet = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
                ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_8_R3Packet);
                break;
            case "v1_9_R1":
                net.minecraft.server.v1_9_R1.PacketPlayOutChat v1_9_R1Packet = new net.minecraft.server.v1_9_R1.PacketPlayOutChat(net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
                ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_9_R1Packet);
                break;
            case "v1_9_R2":
                net.minecraft.server.v1_9_R2.PacketPlayOutChat v1_9_R2Packet = new net.minecraft.server.v1_9_R2.PacketPlayOutChat(net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
                ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_9_R2Packet);
                break;
            case "v1_10_R1":
                net.minecraft.server.v1_10_R1.PacketPlayOutChat v1_10_R1Packet = new net.minecraft.server.v1_10_R1.PacketPlayOutChat(net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
                ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_10_R1Packet);
                break;
            case "v1_11_R1":
                net.minecraft.server.v1_11_R1.PacketPlayOutChat v1_11_R1Packet = new net.minecraft.server.v1_11_R1.PacketPlayOutChat(net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
                ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_11_R1Packet);
                break;
            case "v1_12_R1":
                net.minecraft.server.v1_12_R1.PacketPlayOutChat v1_12_R1Packet = new net.minecraft.server.v1_12_R1.PacketPlayOutChat(net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_12_R1.ChatMessageType.GAME_INFO);
                ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_12_R1Packet);
                break;
            case "v1_13_R1":
                net.minecraft.server.v1_13_R1.PacketPlayOutChat v1_13_R1Packet = new net.minecraft.server.v1_13_R1.PacketPlayOutChat(net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_13_R1.ChatMessageType.GAME_INFO);
                ((org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_13_R1Packet);
                break;
            case "v1_13_R2":
                net.minecraft.server.v1_13_R2.PacketPlayOutChat v1_13_R2Packet = new net.minecraft.server.v1_13_R2.PacketPlayOutChat(net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_13_R2.ChatMessageType.GAME_INFO);
                ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_13_R2Packet);
                break;
            case "v1_14_R1":
                net.minecraft.server.v1_14_R1.PacketPlayOutChat v1_14_R1Packet = new net.minecraft.server.v1_14_R1.PacketPlayOutChat(net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_14_R1.ChatMessageType.GAME_INFO);
                ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_14_R1Packet);
                break;
            case "v1_15_R1":
                net.minecraft.server.v1_15_R1.PacketPlayOutChat v1_15_R1Packet = new net.minecraft.server.v1_15_R1.PacketPlayOutChat(net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_15_R1.ChatMessageType.GAME_INFO);
                ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_15_R1Packet);
                break;
            case "v1_16_R1":
                net.minecraft.server.v1_16_R1.PacketPlayOutChat v1_16_R1Packet = new net.minecraft.server.v1_16_R1.PacketPlayOutChat(net.minecraft.server.v1_16_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_16_R1.ChatMessageType.GAME_INFO, p.getUniqueId());
                ((org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_16_R1Packet);
                break;
            case "v1_16_R2":
                net.minecraft.server.v1_16_R2.PacketPlayOutChat v1_16_R2Packet = new net.minecraft.server.v1_16_R2.PacketPlayOutChat(net.minecraft.server.v1_16_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_16_R2.ChatMessageType.GAME_INFO, p.getUniqueId());
                ((org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_16_R2Packet);
                break;
            case "v1_16_R3":
                net.minecraft.server.v1_16_R3.PacketPlayOutChat v1_16_R3Packet = new net.minecraft.server.v1_16_R3.PacketPlayOutChat(net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), net.minecraft.server.v1_16_R3.ChatMessageType.GAME_INFO, p.getUniqueId());
                ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_16_R3Packet);
                break;
            case "v1_17_R1":
            case "v1_18_R1":
            case "v1_18_R2":
            case "v1_19_R1":
                try {
                    Method sendMessage = p.spigot().getClass().getMethod("sendMessage", ChatMessageType.class, String.class);
                    sendMessage.setAccessible(true);
                    sendMessage.invoke(p.spigot(), ChatMessageType.ACTION_BAR, text);
                }catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getLogger().severe("Failed to send action bar message to player " + p.getName() + "!");
                }
                break;
            default:
                Bukkit.getLogger().severe("Failed to send action bar message to player " + p.getName() + "! Could not determine version");
                break;
        }
    }

    public void sendToAll() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) { sendToPlayer(p); }
    }
}
