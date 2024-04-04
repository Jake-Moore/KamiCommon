package com.kamikazejam.kamicommon.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The same as {@link StringUtil} except with methods for players and compatible with Bukkit
 */
@SuppressWarnings("unused")
public class StringUtilP extends StringUtil {
    public static String p(@Nullable OfflinePlayer player, String s) {
        s = PlaceholderAPI.setPlaceholders(player, s);
        return t(s);
    }

    public static List<String> p(@Nullable OfflinePlayer player, List<String> msg) {
        return p(player, msg.toArray(new String[0]));
    }

    public static List<String> p(@Nullable OfflinePlayer player, String... msg) {
        List<String> strings = new ArrayList<>();
        for (String s : msg) {
            strings.add(p(player, s));
        }
        return strings;
    }

}
