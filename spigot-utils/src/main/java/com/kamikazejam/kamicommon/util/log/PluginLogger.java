package com.kamikazejam.kamicommon.util.log;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@Getter
public class PluginLogger extends LoggerService {
    private final KamiPlugin plugin;
    @Setter
    private boolean debug = false;
    public PluginLogger(KamiPlugin plugin) {
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
    public final void logToConsole(String msg, Level level) {
        // Add the plugin name to the VERY start, so it matches existing logging format
        String plPrefix = "[" + getPlugin().getName() + "] ";

        if (level == Level.FINE) {
            Bukkit.getConsoleSender().sendMessage(StringUtil.t("&7[DEBUG] " + plPrefix + msg));
        } else if (level == Level.INFO) {
            // No need to colorize INFO messages or include the prefix
            Bukkit.getConsoleSender().sendMessage(StringUtil.t(plPrefix + msg));
        } else if (level == Level.WARNING) {
            Bukkit.getConsoleSender().sendMessage(StringUtil.t("&e[WARNING] " + plPrefix + msg));
        } else if (level == Level.SEVERE) {
            Bukkit.getConsoleSender().sendMessage(StringUtil.t("&c[SEVERE] " + plPrefix + msg));
        }
    }
}
