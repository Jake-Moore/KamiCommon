package com.kamikazejamplugins.kamicommon.util;

import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MessageBuilder {
    @Getter private final List<String> lines = new ArrayList<>();

    public static MessageBuilder of(KamiConfig config, String key) {
        return new MessageBuilder(config, key);
    }

    public static MessageBuilder of(ConfigurationSection section, String key) {
        return new MessageBuilder(section, key);
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
     * Replaces all instances of a string with another string
     * @param key The string to replace
     * @param value The string to replace it with
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder replace(String key, String value) {
        List<String> newLines = new ArrayList<>();
        for (String line : this.lines) {
            newLines.add(line.replace(key, value));
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
    public MessageBuilder send(CommandSender sender) {
        for (String s : lines) {
            sender.sendMessage(StringUtil.t(s));
        }
        return this;
    }

    /**
     * Sends the message to a CommandSender
     * @param senders The CommandSender(s) to send the message to
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder send(CommandSender... senders) {
        for (CommandSender c : senders) { send(c); }
        return this;
    }
}
