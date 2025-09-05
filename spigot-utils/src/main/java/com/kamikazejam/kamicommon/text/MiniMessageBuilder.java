package com.kamikazejam.kamicommon.text;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.StringUtilP;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A cross-version MiniMessage parser and messenger.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MiniMessageBuilder {
    private final @NotNull List<VersionedComponent> lines = new ArrayList<>();
    @Setter private boolean translatePAPI = true;

    private MiniMessageBuilder(@NotNull VersionedComponent line) {
        this.lines.add(line);
    }

    private MiniMessageBuilder(@NotNull Collection<VersionedComponent> lines) {
        this.lines.addAll(lines);
    }

    // -------------------------------------------------- //
    //             MiniMessageBuilder METHODS             //
    // -------------------------------------------------- //
    /**
     * Performs a plain text replacement on all lines in this builder.
     * @return This builder, for chaining.
     */
    @NotNull
    public MiniMessageBuilder replace(@NotNull String target, @NotNull String replacement) {
        Preconditions.checkNotNull(target, "target cannot be null");
        Preconditions.checkNotNull(replacement, "replacement cannot be null");
        List<VersionedComponent> newLines = new ArrayList<>();
        for (VersionedComponent line : this.lines) {
            String miniMessage = line.serializeMiniMessage().replace(target, replacement);
            newLines.add(NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessage));
        }
        this.lines.clear();
        this.lines.addAll(newLines);
        return this;
    }

    /**
     * Performs a plain text line replacement for all lines matching the target in this builder.<br>
     * More specifically, every line that matches the target (case-insensitive, color-stripped) will be replaced with the provided replacement lines.
     * @see #replaceLine(String, List, boolean)
     * @return This builder, for chaining.
     */
    @NotNull
    public MiniMessageBuilder replaceLine(@NotNull String target, @NotNull List<VersionedComponent> replacement) {
        return replaceLine(target, replacement, false);
    }

    /**
     * Performs a plain text line replacement for all lines matching the target in this builder.<br>
     * More specifically, every line that matches the target (color-stripped) will be replaced with the provided replacement lines.
     * @param caseSensitive If true, the match will be case-sensitive. If false, it will be case-insensitive.
     * @return This builder, for chaining.
     */
    @NotNull
    public MiniMessageBuilder replaceLine(@NotNull String target, @NotNull List<VersionedComponent> replacement, boolean caseSensitive) {
        Preconditions.checkNotNull(target, "target cannot be null");
        Preconditions.checkNotNull(replacement, "replacement cannot be null");
        List<VersionedComponent> newLines = new ArrayList<>();
        for (VersionedComponent line : this.lines) {
            String plainText = line.plainText();
            if (caseSensitive ? plainText.equals(target) : plainText.equalsIgnoreCase(target)) {
                newLines.addAll(replacement);
            } else {
                newLines.add(line);
            }
        }
        this.lines.clear();
        this.lines.addAll(newLines);
        return this;
    }

    /**
     * Performs a plain text line replacement for all lines <strong>containing</strong> the target in this builder.<br>
     * More specifically, every line that contains the target (case-insensitive, color-stripped) will be replaced with the provided replacement lines.
     * @see #replaceLineContains(String, List, boolean)
     * @return This builder, for chaining.
     */
    @NotNull
    public MiniMessageBuilder replaceLineContains(@NotNull String target, @NotNull List<VersionedComponent> replacement) {
        return replaceLineContains(target, replacement, false);
    }

    /**
     * Performs a plain text line replacement for all lines <strong>containing</strong> the target in this builder.<br>
     * More specifically, every line that contains the target (color-stripped) will be replaced with the provided replacement lines.
     * @param caseSensitive If true, the match will be case-sensitive. If false, it will be case-insensitive.
     * @return This builder, for chaining.
     */
    @NotNull
    public MiniMessageBuilder replaceLineContains(@NotNull String target, @NotNull List<VersionedComponent> replacement, boolean caseSensitive) {
        Preconditions.checkNotNull(target, "target cannot be null");
        Preconditions.checkNotNull(replacement, "replacement cannot be null");
        List<VersionedComponent> newLines = new ArrayList<>();
        for (VersionedComponent line : this.lines) {
            String plainText = line.plainText();
            if (caseSensitive ? plainText.contains(target) : plainText.toLowerCase().contains(target.toLowerCase())) {
                newLines.addAll(replacement);
            } else {
                newLines.add(line);
            }
        }
        this.lines.clear();
        this.lines.addAll(newLines);
        return this;
    }



    // -------------------------------------------------- //
    //                   Sending METHODS                  //
    // -------------------------------------------------- //
    /**
     * Sends the message to a CommandSender
     * @param sender The CommandSender to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    @NotNull
    public MiniMessageBuilder send(@NotNull CommandSender sender) {
        Preconditions.checkNotNull(sender, "sender cannot be null");
        @Nullable Player player = (sender instanceof Player) ? (Player) sender : null;

        // Handle PAPI
        if (translatePAPI) {
            for (VersionedComponent component : lines) {
                // Direct PAPI parse on the MiniMessage string itself
                String miniMessage = StringUtilP.justP(player, component.serializeMiniMessage());
                // Re-parse the MiniMessage string into a component
                VersionedComponent parsed = NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessage);
                // Send the parsed component
                parsed.sendTo(sender);
            }
            return this;
        }

        // Direct send
        this.lines.forEach(component -> component.sendTo(sender));
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param senders The CommandSender to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    @NotNull
    public MiniMessageBuilder send(@NotNull CommandSender... senders) {
        Preconditions.checkNotNull(senders, "sender cannot be null");
        for (CommandSender s : senders) { send(s); }
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param senders The CommandSender to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    @NotNull
    public MiniMessageBuilder send(@NotNull List<CommandSender> senders) {
        Preconditions.checkNotNull(senders, "sender cannot be null");
        for (CommandSender s : senders) { send(s); }
        return this;
    }

    /**
     * Sends the message to all Online Players
     * @param consoleToo If true, the message will also be sent to the console
     */
    @NotNull
    public MiniMessageBuilder sendAll(boolean consoleToo) {
        for (Player p : Bukkit.getOnlinePlayers()) { send(p); }
        if (consoleToo) { send(Bukkit.getConsoleSender()); }
        return this;
    }

    /**
     * Sends the message to all Online Players (and console)
     */
    @NotNull
    public MiniMessageBuilder sendAll() {
        return sendAll(true);
    }



    // -------------------------------------------------- //
    //                 DIRECT CONSTRUCTION                //
    // -------------------------------------------------- //

    /**
     * Parses a MiniMessage string into a {@link MiniMessageBuilder} containing the parsed line.
     * <br>
     * Does not convert or support legacy codes (&amp; or &sect;). See {@link #fromLegacyAmpersand(String)} and {@link #fromLegacySection(String)} for those.
     */
    public static @NotNull MiniMessageBuilder fromMiniMessage(@NotNull String miniMessage) {
        return new MiniMessageBuilder(NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessage));
    }

    /**
     * Parses a legacy ampersand (&amp;) string into a {@link MiniMessageBuilder} containing the parsed line.
     * <br>
     * Will ignore section (&sect;) codes. See {@link #fromLegacySection(String)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacyAmpersand(@NotNull String legacy) {
        return new MiniMessageBuilder(NmsAPI.getVersionedComponentSerializer().fromLegacyAmpersand(legacy));
    }

    /**
     * Parses a legacy section (&sect;) string into a {@link MiniMessageBuilder} containing the parsed line.
     * <br>
     * Will ignore ampersand (&amp;) codes. See {@link #fromLegacyAmpersand(String)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacySection(@NotNull String legacy) {
        return new MiniMessageBuilder(NmsAPI.getVersionedComponentSerializer().fromLegacySection(legacy));
    }

    // -------------------------------------------------- //
    //          DIRECT CONSTRUCTION (MULTI-LINE)          //
    // -------------------------------------------------- //

    /**
     * Parses a collection of MiniMessage strings (treated as individual lines) into a {@link MiniMessageBuilder} containing each parsed line.
     * <br>
     * Does not convert or support legacy codes (&amp; or &sect;). See {@link #fromLegacyAmpersand(Collection)} and {@link #fromLegacySection(Collection)} for those.
     */
    public static @NotNull MiniMessageBuilder fromMiniMessage(@NotNull Collection<String> miniMessageLines) {
        List<VersionedComponent> components = new ArrayList<>();
        for (String line : miniMessageLines) {
            components.add(NmsAPI.getVersionedComponentSerializer().fromMiniMessage(line));
        }
        return new MiniMessageBuilder(components);
    }

    /**
     * Parses a series of MiniMessage strings (treated as individual lines) into a {@link MiniMessageBuilder} containing each parsed line.
     * <br>
     * Does not convert or support legacy codes (&amp; or &sect;). See {@link #fromLegacyAmpersand(String...)} and {@link #fromLegacySection(String...)} for those.
     */
    public static @NotNull MiniMessageBuilder fromMiniMessage(@NotNull String... miniMessageLines) {
        List<VersionedComponent> components = new ArrayList<>();
        for (String line : miniMessageLines) {
            components.add(NmsAPI.getVersionedComponentSerializer().fromMiniMessage(line));
        }
        return new MiniMessageBuilder(components);
    }

    /**
     * Parses a collection of legacy ampersand (&amp;) strings (treated as individual lines) into a {@link MiniMessageBuilder} containing each parsed line.
     * <br>
     * Will ignore section (&sect;) codes. See {@link #fromLegacySection(Collection)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacyAmpersand(@NotNull Collection<String> legacyLines) {
        List<VersionedComponent> components = new ArrayList<>();
        for (String line : legacyLines) {
            components.add(NmsAPI.getVersionedComponentSerializer().fromLegacyAmpersand(line));
        }
        return new MiniMessageBuilder(components);
    }

    /**
     * Parses a series of legacy ampersand (&amp;) strings (treated as individual lines) into a {@link MiniMessageBuilder} containing each parsed line.
     * <br>
     * Will ignore section (&sect;) codes. See {@link #fromLegacySection(String...)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacyAmpersand(@NotNull String... legacyLines) {
        List<VersionedComponent> components = new ArrayList<>();
        for (String line : legacyLines) {
            components.add(NmsAPI.getVersionedComponentSerializer().fromLegacyAmpersand(line));
        }
        return new MiniMessageBuilder(components);
    }

    /**
     * Parses a collection of legacy section (&sect;) strings (treated as individual lines) into a {@link MiniMessageBuilder} containing each parsed line.
     * <br>
     * Will ignore ampersand (&amp;) codes. See {@link #fromLegacyAmpersand(Collection)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacySection(@NotNull Collection<String> legacyLines) {
        List<VersionedComponent> components = new ArrayList<>();
        for (String line : legacyLines) {
            components.add(NmsAPI.getVersionedComponentSerializer().fromLegacySection(line));
        }
        return new MiniMessageBuilder(components);
    }

    /**
     * Parses a series of legacy section (&sect;) strings (treated as individual lines) into a {@link MiniMessageBuilder} containing each parsed line.
     * <br>
     * Will ignore ampersand (&amp;) codes. See {@link #fromLegacyAmpersand(String...)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacySection(@NotNull String... legacyLines) {
        List<VersionedComponent> components = new ArrayList<>();
        for (String line : legacyLines) {
            components.add(NmsAPI.getVersionedComponentSerializer().fromLegacySection(line));
        }
        return new MiniMessageBuilder(components);
    }

    // -------------------------------------------------- //
    //              BUKKIT CONFIG CONSTRUCTION            //
    // -------------------------------------------------- //

    /**
     * Parses a MiniMessage formatted message from the config located at the provided key.<br>
     * <br>
     * Both {@code String} and {@code List<String>} are supported types for the config value.<br>
     * For list messages, the returned {@link MiniMessageBuilder} will contain each list entry as a separate line (component).<br>
     * (Single String config values are returned as a builder with only one line.)<br>
     * <br>
     * Does not convert or support legacy codes (&amp; or &sect;). See {@link #fromLegacyAmpersand(org.bukkit.configuration.ConfigurationSection, String)} and {@link #fromLegacySection(ConfigurationSection, String)} for those.
     */
    public static @NotNull MiniMessageBuilder fromMiniMessage(@NotNull org.bukkit.configuration.ConfigurationSection section, @NotNull String key) {
        Preconditions.checkNotNull(section, "section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        if (section.isString(key)) {
            return fromMiniMessage(section.getString(key));
        } else if (section.isList(key)) {
            return fromMiniMessage(section.getStringList(key));
        }
        throw new IllegalArgumentException("Config Key is not a string or list: " + key);
    }

    /**
     * Parses a legacy ampersand (&amp;) formatted message from the config located at the provided key.<br>
     * <br>
     * Both {@code String} and {@code List<String>} are supported types for the config value.<br>
     * For list messages, the returned {@link MiniMessageBuilder} will contain each list entry as a separate line (component).<br>
     * (Single String config values are returned as a builder with only one line.)<br>
     * <br>
     * Will ignore section (&sect;) codes. See {@link #fromLegacySection(org.bukkit.configuration.ConfigurationSection, String)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacyAmpersand(@NotNull org.bukkit.configuration.ConfigurationSection section, @NotNull String key) {
        Preconditions.checkNotNull(section, "section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        if (section.isString(key)) {
            return fromLegacyAmpersand(section.getString(key));
        } else if (section.isList(key)) {
            return fromLegacyAmpersand(section.getStringList(key));
        }
        throw new IllegalArgumentException("Config Key is not a string or list: " + key);
    }

    /**
     * Parses a legacy section (&sect;) formatted message from the config located at the provided key.<br>
     * <br>
     * Both {@code String} and {@code List<String>} are supported types for the config value.<br>
     * For list messages, the returned {@link MiniMessageBuilder} will contain each list entry as a separate line (component).<br>
     * (Single String config values are returned as a builder with only one line.)<br>
     * <br>
     * Will ignore ampersand (&amp;) codes. See {@link #fromLegacyAmpersand(org.bukkit.configuration.ConfigurationSection, String)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacySection(@NotNull org.bukkit.configuration.ConfigurationSection section, @NotNull String key) {
        Preconditions.checkNotNull(section, "section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        if (section.isString(key)) {
            return fromLegacySection(section.getString(key));
        } else if (section.isList(key)) {
            return fromLegacySection(section.getStringList(key));
        }
        throw new IllegalArgumentException("Config Key is not a string or list: " + key);
    }

    // -------------------------------------------------- //
    //               KAMI CONFIG CONSTRUCTION             //
    // -------------------------------------------------- //

    /**
     * Parses a MiniMessage formatted message from the config located at the provided key.<br>
     * <br>
     * Both {@code String} and {@code List<String>} are supported types for the config value.<br>
     * For list messages, the returned {@link MiniMessageBuilder} will contain each list entry as a separate line (component).<br>
     * (Single String config values are returned as a builder with only one line.)<br>
     * <br>
     * Does not convert or support legacy codes (&amp; or &sect;). See {@link #fromLegacyAmpersand(ConfigurationSection, String)} and {@link #fromLegacySection(ConfigurationSection, String)} for those.
     */
    public static @NotNull MiniMessageBuilder fromMiniMessage(@NotNull ConfigurationSection section, @NotNull String key) {
        Preconditions.checkNotNull(section, "section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        if (section.isString(key)) {
            return fromMiniMessage(section.getString(key));
        } else if (section.isList(key)) {
            return fromMiniMessage(section.getStringList(key));
        }
        throw new IllegalArgumentException("Config Key is not a string or list: " + key);
    }

    /**
     * Parses a legacy ampersand (&amp;) formatted message from the config located at the provided key.<br>
     * <br>
     * Both {@code String} and {@code List<String>} are supported types for the config value.<br>
     * For list messages, the returned {@link MiniMessageBuilder} will contain each list entry as a separate line (component).<br>
     * (Single String config values are returned as a builder with only one line.)<br>
     * <br>
     * Will ignore section (&sect;) codes. See {@link #fromLegacySection(ConfigurationSection, String)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacyAmpersand(@NotNull ConfigurationSection section, @NotNull String key) {
        Preconditions.checkNotNull(section, "section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        if (section.isString(key)) {
            return fromLegacyAmpersand(section.getString(key));
        } else if (section.isList(key)) {
            return fromLegacyAmpersand(section.getStringList(key));
        }
        throw new IllegalArgumentException("Config Key is not a string or list: " + key);
    }

    /**
     * Parses a legacy section (&sect;) formatted message from the config located at the provided key.<br>
     * <br>
     * Both {@code String} and {@code List<String>} are supported types for the config value.<br>
     * For list messages, the returned {@link MiniMessageBuilder} will contain each list entry as a separate line (component).<br>
     * (Single String config values are returned as a builder with only one line.)<br>
     * <br>
     * Will ignore ampersand (&amp;) codes. See {@link #fromLegacyAmpersand(ConfigurationSection, String)} for that.
     */
    public static @NotNull MiniMessageBuilder fromLegacySection(@NotNull ConfigurationSection section, @NotNull String key) {
        Preconditions.checkNotNull(section, "section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        if (section.isString(key)) {
            return fromLegacySection(section.getString(key));
        } else if (section.isList(key)) {
            return fromLegacySection(section.getStringList(key));
        }
        throw new IllegalArgumentException("Config Key is not a string or list: " + key);
    }
}
