package com.kamikazejam.kamicommon.nms.abstraction.chat;

import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Action;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageBlock;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractMessageManager {

    /**
     * This will process the message, replace stuff, add events, and then send it.
     * @param sender The {@link CommandSender} to send the messages to
     * @param message The {@link KMessageSingle} to process and send
     */
    public abstract void processAndSend(@NotNull CommandSender sender, @NotNull KMessage message);

    /**
     * This will process the message, replace stuff, add events, and then send it.
     * @param sender The {@link CommandSender} to send the messages to
     * @param messages The {@link KMessageSingle}s to process and send
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull List<KMessage> messages) {
        messages.forEach(message -> this.processAndSend(sender, message));
    }

    /**
     * This will process the message, replace stuff, add events, and then send it.
     * @param sender The {@link CommandSender} to send the messages to
     * @param messages The {@link KMessageSingle}s to process and send
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull KMessage... messages) {
        this.processAndSend(sender, List.of(messages));
    }

    /**
     * This will process the message, replace stuff, add events, and then send it.
     * @param sender The {@link CommandSender} to send the messages to
     * @param line The line to process and send
     * @param actions The actions to add
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull String line, @NotNull Action... actions) {
        this.processAndSend(sender, new KMessageSingle(line, actions));
    }

    /**
     * This will process the message, replace stuff, add events, and then send it.
     * @param sender The {@link CommandSender} to send the messages to
     * @param line The line to process and send
     * @param actions The actions to add
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull String line, boolean translate, @NotNull Action... actions) {
        this.processAndSend(sender, new KMessageSingle(line).setTranslate(translate));
    }

    /**
     * This will process the message, replace stuff, add events, and then send it.
     * @param sender The {@link CommandSender} to send the messages to
     * @param lines The lines to process and send
     * @param actions The actions to add
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull List<String> lines, @NotNull Action... actions) {
        this.processAndSend(sender, new KMessageBlock(lines, actions));
    }
}
