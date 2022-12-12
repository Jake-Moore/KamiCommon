package com.kamikazejamplugins.kamicommon.util.actionbar;

import net.minecraft.server.v1_8_R1.ChatComponentText;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

class ActionBar_1_8_R1 implements ActionBar {

    @Override
    public void sendToPlayer(Player p, String text) {
        PacketPlayOutChat v1_8_R1Packet = new PacketPlayOutChat(new ChatComponentText(text), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_8_R1Packet);
    }

    @Override
    public void sendToAll(String text) { for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) { sendToPlayer(p, text); } }
}
