package com.kamikazejam.kamicommon.nms.chatcolor;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class ChatColor1_16_R2 implements IChatColorNMS {
    @Override
    public @NotNull Color getColor(ChatColor chatColor) {
        return chatColor.asBungee().getColor();
    }
}
