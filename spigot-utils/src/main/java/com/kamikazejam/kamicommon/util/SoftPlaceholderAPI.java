package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.integrations.PlaceholderAPIIntegration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A utility class for soft PlaceholderAPI integration<br>
 * <br>
 * This class will utilize PlaceholderAPI if it is present on the server, safely ignoring replacements if it is not.
 */
@SuppressWarnings("unused")
public class SoftPlaceholderAPI {
    /**
     * Attempts to set placeholders in the given string for the given player if PlaceholderAPI is present<br>
     * If PlaceholderAPI is not present, the string is returned unchanged<br>
     * <br>
     * This method will warn in the console if PlaceholderAPI is not found and {@link Config#isWarnAboutPlaceholders()} is true
     */
    @NotNull
    public static String setPlaceholders(@Nullable OfflinePlayer player, String s) {
        @Nullable PlaceholderAPIIntegration papi = SpigotUtilsSource.getPlaceholderIntegration();
        if (papi != null) {
            return papi.setPlaceholders(player, s);
        }else {
            if (Config.isWarnAboutPlaceholders()) {
                SpigotUtilsSource.get().getLogger().warning("PlaceholderAPI not found! This may cause issues with placeholders!");
            }
            return s;
        }
    }

    /**
     * Attempts to set placeholders in the given list of strings for the given player if PlaceholderAPI is present<br>
     * If PlaceholderAPI is not present, the list is returned unchanged<br>
     * <br>
     * This method will warn in the console if PlaceholderAPI is not found and {@link Config#isWarnAboutPlaceholders()} is true
     */
    @NotNull
    public static List<String> setPlaceholders(@Nullable OfflinePlayer player, @NotNull List<String> strings) {
        return strings.stream().map(s -> setPlaceholders(player, s)).toList();
    }

    @Configurable
    public static class Config {
        /**
         * Toggle the warning messages when PlaceholderAPI is not found and placeholders are attempted to be set
         */
        @Getter @Setter
        private static boolean warnAboutPlaceholders = true;
    }
}
