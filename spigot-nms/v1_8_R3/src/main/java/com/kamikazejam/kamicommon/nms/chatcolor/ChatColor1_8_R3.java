package com.kamikazejam.kamicommon.nms.chatcolor;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import com.kamikazejam.kamicommon.util.Colors;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public class ChatColor1_8_R3 implements IChatColorNMS {
    @Override
    public @Nullable Color getColor(org.bukkit.ChatColor chatColor) {
        try {
            // Use an enum version of bungee's ChatColor class which has the hex codes
            Colors c = Colors.valueOf(chatColor.name().toUpperCase());
            return c.getColor();
        }catch (Throwable ignored) {
            return null;
        }
    }
}
