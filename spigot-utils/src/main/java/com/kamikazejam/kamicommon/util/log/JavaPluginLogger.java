package com.kamikazejam.kamicommon.util.log;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class JavaPluginLogger extends LoggerService {
    private final JavaPlugin plugin;
    @Setter
    private boolean debug = false;
    public JavaPluginLogger(JavaPlugin plugin) {
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
        String plPrefix = "[" + plugin.getName() + "] ";
        String content = plPrefix + message;

        if (level == Level.FINE) {
            plugin.getLogger().fine(content);
        } else if (level == Level.INFO) {
            plugin.getLogger().info(content);
        } else if (level == Level.WARNING) {
            plugin.getLogger().warning(content);
        } else if (level == Level.SEVERE) {
            plugin.getLogger().severe(content);
        } else {
            plugin.getLogger().log(level, content);
        }
    }
}
