package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.ApiStatus.Obsolete;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Legacy methods related to {@link ChatColor} and legacy color codes.
 */
@Obsolete
@SuppressWarnings("unused")
public class LegacyChatColors {
    /**
     * Map a hex string to the closest matching {@link ChatColor}.
     */
    @Obsolete
    public static @NotNull ChatColor getNearestChatColor(@NotNull String hex) {
        Preconditions.checkNotNull(hex, "hex cannot be null");
        return getNearestChatColor(new Color(Integer.decode(hex)));
    }

    /**
     * Map a hex string to the closest matching {@link ChatColor}.
     */
    @Obsolete
    public static @NotNull ChatColor getNearestChatColor(@NotNull Color color) {
        Preconditions.checkNotNull(color, "color cannot be null");

        @NotNull ChatColor nearest = ChatColor.WHITE;
        @Nullable Double distance = null;

        for (ChatColor chatColor : ChatColor.values()) {

            if (!chatColor.isColor()) continue;

            @Nullable Color checkColor = NmsAPI.getJavaColor(chatColor);
            if (checkColor == null) continue;
            int deltaR = color.getRed() - checkColor.getRed();
            int deltaG = color.getGreen() - checkColor.getGreen();
            int deltaB = color.getBlue() - checkColor.getBlue();

            double delta = Math.sqrt((deltaR * deltaR) + (deltaG * deltaG) + (deltaB * deltaB));
            if (distance == null || delta < distance) {
                nearest = chatColor;
                distance = delta;
            }
        }

        return nearest;
    }

    /**
     * Map a {@link DyeColor} to the closest matching {@link ChatColor}.
     *
     * @throws RuntimeException if the {@link DyeColor} is unknown (should never happen)
     */
    @Obsolete
    @Contract(pure = true)
    public static @NotNull ChatColor getChatColor(@NotNull DyeColor dyeColor) {
        if (dyeColor.equals(DyeColor.WHITE)) {
            return ChatColor.WHITE;
        } else if (dyeColor.equals(DyeColor.ORANGE)) {
            return ChatColor.GOLD;
        } else if (dyeColor.equals(DyeColor.MAGENTA) || dyeColor.equals(DyeColor.PINK)) {
            return ChatColor.LIGHT_PURPLE;
        } else if (dyeColor.equals(DyeColor.LIGHT_BLUE)) {
            return ChatColor.AQUA;
        } else if (dyeColor.equals(DyeColor.YELLOW)) {
            return ChatColor.YELLOW;
        } else if (dyeColor.equals(DyeColor.LIME)) {
            return ChatColor.GREEN;
        } else if (dyeColor.equals(DyeColor.GRAY)) {
            return ChatColor.DARK_GRAY;
        } else if (dyeColor.name().equalsIgnoreCase("LIGHT_GRAY") || dyeColor.equals(DyeColor.BROWN)) {
            return ChatColor.GRAY;
        } else if (dyeColor.equals(DyeColor.CYAN)) {
            return ChatColor.DARK_AQUA;
        } else if (dyeColor.equals(DyeColor.PURPLE)) {
            return ChatColor.DARK_PURPLE;
        } else if (dyeColor.equals(DyeColor.BLUE)) {
            return ChatColor.BLUE;
        } else if (dyeColor.equals(DyeColor.GREEN)) {
            return ChatColor.DARK_GREEN;
        } else if (dyeColor.equals(DyeColor.RED)) {
            return ChatColor.RED;
        } else if (dyeColor.equals(DyeColor.BLACK)) {
            return ChatColor.BLACK;
        } else {
            throw new RuntimeException("Unknown DyeColor " + dyeColor);
        }
    }
}
