package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.configuration.config.KamiConfig;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A utility class for sending building messages built from a config. <p>
 * The primary function of this class is to grab either a string or string list from the config. <p>
 * It will detect which one to use and the server owner can configure it as either. <p>
 * The secondary function of this class is to send messages translated, and with PAPI placeholders replaced
 */
@Getter @Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MessageBuilder {
    private final List<String> lines = new ArrayList<>();
    @Setter private boolean translateColor = true;

    public static MessageBuilder of(String message) {
        return new MessageBuilder(message);
    }

    public static MessageBuilder of(List<String> lines) {
        return new MessageBuilder(lines);
    }

    public static MessageBuilder of(String... lines) {
        return new MessageBuilder(lines);
    }

    public static MessageBuilder of(org.bukkit.configuration.ConfigurationSection config, String key) {
        return new MessageBuilder(config, key);
    }

    public static MessageBuilder of(FileConfiguration config, String key) {
        return new MessageBuilder(config, key);
    }

    public static MessageBuilder of(KamiConfig config, String key) {
        return new MessageBuilder(config, key);
    }

    public static MessageBuilder of(ConfigurationSection section, String key) {
        return new MessageBuilder(section, key);
    }


    /**
     * Creates a new MessageBuilder from a string
     * @param message The message to send
     */
    public MessageBuilder(String message) {
        this.lines.add(message);
    }

    /**
     * Creates a new MessageBuilder from a list of strings
     * @param lines The lines to send
     */
    public MessageBuilder(Collection<String> lines) {
        this.lines.addAll(lines);
    }

    /**
     * Creates a new MessageBuilder from a list of strings
     * @param lines The lines to send
     */
    public MessageBuilder(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
    }

    /**
     * Creates a new MessageBuilder from a configuration key
     * @param section The section to search the key in
     * @param key The key pointing to the message.
     * This class will detect if the key points to a string or a list of strings,
     *   and then handle things accordingly.
     */
    public MessageBuilder(ConfigurationSection section, String key) {
        if (section.isString(key)) {
            this.lines.add(section.getString(key));
        } else {
            this.lines.addAll(section.getStringList(key));
        }
    }

    /**
     * Creates a new MessageBuilder from a configuration key
     * @param config The KamiConfig to search the key in
     * @param key The key pointing to the message.
     * This class will detect if the key points to a string or a list of strings,
     *   and then handle things accordingly.
     */
    public MessageBuilder(KamiConfig config, String key) {
        if (config.isString(key)) {
            this.lines.add(config.getString(key));
        } else {
            this.lines.addAll(config.getStringList(key));
        }
    }

    /**
     * Creates a new MessageBuilder from a configuration key
     * @param config The ConfigurationSection to search the key in
     * @param key The key pointing to the message.
     * This class will detect if the key points to a string or a list of strings,
     *   and then handle things accordingly.
     */
    public MessageBuilder(org.bukkit.configuration.ConfigurationSection config, String key) {
        if (config.isString(key)) {
            this.lines.add(config.getString(key));
        } else {
            this.lines.addAll(config.getStringList(key));
        }
    }

    /**
     * Creates a new MessageBuilder from a configuration key
     * @param config The FileConfiguration to search the key in
     * @param key The key pointing to the message.
     * This class will detect if the key points to a string or a list of strings,
     *   and then handle things accordingly.
     */
    public MessageBuilder(FileConfiguration config, String key) {
        if (config.isString(key)) {
            this.lines.add(config.getString(key));
        } else {
            this.lines.addAll(config.getStringList(key));
        }
    }

    /**
     * Replaces all instances of a string with another string
     * @param key The string to replace
     * @param value The string to replace it with
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder replace(@NotNull String key, @NotNull String value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        List<String> newLines = new ArrayList<>();
        for (String line : this.lines) {
            newLines.add(line.replace(key, value));
        }
        this.lines.clear();
        this.lines.addAll(newLines);
        return this;
    }

    /**
     * Searches for the find placeholder in the text, and replaces that entire line with replacement
     * @param find The string to search for in the lore
     * @param replacement The lines to swap in, in place of the entire line containing find
     * @return The IBuilder with replaced lore
     */
    public MessageBuilder replaceLine(String find, List<String> replacement) {
        List<String> newLines = new ArrayList<>();
        for (String line : this.lines) {
            if (ChatColor.stripColor(line).contains(ChatColor.stripColor(find))) {
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
     * Sends the message to a CommandSender
     * @param sender The CommandSender to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder send(@Nonnull CommandSender sender) {
        if (sender instanceof Player) { send((Player) sender); return this; }

        for (String s : lines) {
            if (translateColor) {
                sender.sendMessage(StringUtil.t(s));
            }else {
                sender.sendMessage(s);
            }
        }
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param senders The CommandSender to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder send(@Nonnull CommandSender... senders) {
        for (CommandSender s : senders) { send(s); }
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param senders The CommandSender to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder send(@Nonnull List<CommandSender> senders) {
        for (CommandSender s : senders) { send(s); }
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param player The Player to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder send(@Nonnull Player player) {
        for (String s : lines) {
            player.sendMessage(StringUtilP.p(player, s));
        }
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param players The Player(s) to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder send(@Nonnull Player... players) {
        for (Player p : players) { send(p); }
        return this;
    }
}
