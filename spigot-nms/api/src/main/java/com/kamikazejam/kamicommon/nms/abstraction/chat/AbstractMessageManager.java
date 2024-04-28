package com.kamikazejam.kamicommon.nms.abstraction.chat;

import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Action;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractMessageManager {
    /**
     * The easiest method to use. This will process the message, replace stuff, add events, and then send it.
     * @param player The player to send the messages to
     * @param line The line to process and send
     * @param actions The actions to add
     */
    public void processAndSend(Player player, String line, Action... actions) {
        this.processAndSend(player, line, true, actions);
    }

    /**
     * The easiest method to use. This will process the message, replace stuff, add events, and then send it.
     * @param player The player to send the messages to
     * @param line The line to process and send
     * @param actions The actions to add
     */
    public abstract void processAndSend(Player player, String line, boolean translate, Action... actions);

    /**
     * The easiest method to use. This will process the message, replace stuff, add events, and then send it.
     * @param player The player to send the messages to
     * @param lines The lines to process and send
     * @param actions The actions to add
     */
    public void processAndSend(Player player, List<String> lines, Action... actions) {
        for (String line : lines) {
            processAndSend(player, line, actions);
        }
    }
}
