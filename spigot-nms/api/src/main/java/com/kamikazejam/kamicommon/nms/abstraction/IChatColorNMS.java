package com.kamikazejam.kamicommon.nms.abstraction;

import org.bukkit.ChatColor;

import javax.annotation.Nullable;
import java.awt.Color;

public interface IChatColorNMS {
    @Nullable Color getColor(ChatColor chatColor);
}
