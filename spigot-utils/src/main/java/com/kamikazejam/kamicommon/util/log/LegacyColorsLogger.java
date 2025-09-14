package com.kamikazejam.kamicommon.util.log;

import com.kamikazejam.kamicommon.util.LegacyColors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

@Getter
public class LegacyColorsLogger extends LoggerService {
    private final Plugin plugin;
    @Setter
    private boolean debug = false;
    public LegacyColorsLogger(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLoggerName() {
        // Unused
        return "";
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public final void logToConsole(String message, Level level) {
        // Add the plugin name to the VERY start, so it matches existing logging format
        String plPrefix = "[" + getPlugin().getName() + "] ";

        if (level == Level.FINE) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&7[DEBUG] " + plPrefix + message));
        } else if (level == Level.INFO) {
            // No need to colorize INFO messages or include the prefix
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t(plPrefix + message));
        } else if (level == Level.WARNING) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&e[WARNING] " + plPrefix + message));
        } else if (level == Level.SEVERE) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&c[SEVERE] " + plPrefix + message));
        }
    }
}
