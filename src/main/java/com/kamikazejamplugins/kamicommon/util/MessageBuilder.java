package com.kamikazejamplugins.kamicommon.util;

import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import com.kamikazejamplugins.kamicommon.yaml.MemorySection;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MessageBuilder {
    private final List<String> lines;

    /**
     * Creates a new MessageBuilder from a configuration key
     * @param section The section to search the key in
     * @param key The key pointing to the message.
     * This class will detect if the key points to a string or a list of strings,
     *   and then handle things accordingly.
     */
    public MessageBuilder(MemorySection section, String key) {
        if (section.isString(key)) {
            this.lines = Collections.singletonList(section.getString(key));
        } else {
            this.lines = section.getStringList(key);
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
            this.lines = Collections.singletonList(config.getString(key));
        } else {
            this.lines = config.getStringList(key);
        }
    }

    /**
     * Replaces all instances of a string with another string
     * @param key The string to replace
     * @param value The string to replace it with
     * @return The MessageBuilder instance (for chaining)
     */
    public MessageBuilder replace(String key, String value) {
        lines.replaceAll(s -> s.replace(key, value));
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
