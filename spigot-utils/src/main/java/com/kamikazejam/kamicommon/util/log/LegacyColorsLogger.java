package com.kamikazejam.kamicommon.util.log;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.LegacyColors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * Simple colored logger that automatically parse legacy color codes using the section symbol (&sect;) or ampersand (&amp;).<br>
 * <br>
 * This logger is designed for use in Bukkit/Spigot/Paper plugins, and will prepend the plugin name to each message.<br>
 * <br>
 * It supports different log levels, and will colorize the output accordingly:<br>
 * - DEBUG (Level.FINE): Gray<br>
 * - INFO (Level.INFO): No colorization<br>
 * - WARNING (Level.WARNING): Yellow<br>
 * - SEVERE (Level.SEVERE): Red<br>
 * <br>
 * Note: The logger does not currently support other log levels.
 */
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

    @Internal
    @Override
    public final void logToConsole(String message, Level level) {
        // Add the plugin name to the VERY start, so it matches existing logging format
        String plPrefix = "[" + getPlugin().getName() + "] ";

        CommandSender console = Bukkit.getConsoleSender();
        @Nullable VersionedComponent prefix = null;

        // Parse the prefix based on the log level
        if (level == Level.FINE) {
            prefix = NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<gray>[DEBUG] ");
        } else if (level == Level.INFO) {
            // No need to colorize INFO messages or include the prefix
            prefix = NmsAPI.getVersionedComponentSerializer().fromPlainText("");
        } else if (level == Level.WARNING) {
            prefix = NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<yellow>[WARNING] ");
        } else if (level == Level.SEVERE) {
            prefix = NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<red>[SEVERE] ");
        }

        if (prefix != null) {
            // Append the prefix and the message, translating ampersand codes and then mapping to a component
            prefix.append(
                    NmsAPI.getVersionedComponentSerializer().fromLegacySection(LegacyColors.t(plPrefix + message))
            ).sendTo(console);
        }
    }
}
