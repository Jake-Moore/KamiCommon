package com.kamikazejamplugins.kamicommon.util.actionbar;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

class ActionBar_1_8_R2 implements ActionBar {

    @Override
    public void sendToPlayer(Player p, String text) {
        PacketPlayOutChat v1_8_R2Packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(v1_8_R2Packet);
    }

    @Override
    public void sendToAll(String text) { for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) { sendToPlayer(p, text); } }
}
