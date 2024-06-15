package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.SpigotUtilProvider;
import com.kamikazejam.kamicommon.integrations.PlaceholderAPIIntegration;
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
        // Replace PAPI placeholders and then translate
        return t(justP(player, s));
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

    public static String justP(@Nullable OfflinePlayer player, String s) {
        @Nullable PlaceholderAPIIntegration papi = SpigotUtilProvider.getPlaceholderIntegration();
        if (papi != null) {
            return papi.setPlaceholders(player, s);
        }else {
            SpigotUtilProvider.getPlugin().getLogger().warning("PlaceholderAPI not found! This may cause issues with placeholders!");
            return s;
        }
    }
}
