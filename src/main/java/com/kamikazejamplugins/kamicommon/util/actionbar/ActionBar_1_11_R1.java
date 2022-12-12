package com.kamikazejamplugins.kamicommon.util.actionbar;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

class ActionBar_1_11_R1 implements ActionBar {

    @Override
    public void sendToPlayer(Player p, String text) {
        PacketPlayOutChat v1_11_R1Packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_11_R1Packet);
    }

    @Override
    public void sendToAll(String text) { for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) { sendToPlayer(p, text); } }
}
