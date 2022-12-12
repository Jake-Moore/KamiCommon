package com.kamikazejamplugins.kamicommon.util.actionbar;

import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

class ActionBar_1_16_R3 implements ActionBar {

    @Override
    public void sendToPlayer(Player p, String text) {
        PacketPlayOutChat v1_16_R3Packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), ChatMessageType.GAME_INFO, p.getUniqueId());
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_16_R3Packet);
    }

    @Override
    public void sendToAll(String text) { for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) { sendToPlayer(p, text); } }
}
