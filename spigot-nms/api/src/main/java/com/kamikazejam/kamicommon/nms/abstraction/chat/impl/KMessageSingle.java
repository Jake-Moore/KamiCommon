package com.kamikazejam.kamicommon.nms.abstraction.chat.impl;

import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A single message (line) that can be sent to a player.
 */
@Setter @Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class KMessageSingle extends KMessage {
    private @NotNull String line;

    public KMessageSingle() {
        super();
        this.line = "";
    }
    public KMessageSingle(@NotNull String line) {
        super();
        this.line = line;
    }
    public KMessageSingle(@NotNull String line, @NotNull Action... actions) {
        super(actions);
        this.line = line;
    }
    public KMessageSingle(@NotNull String line, @NotNull List<Action> actions) {
        super(actions);
        this.line = line;
    }

    @NotNull
    public KMessageSingle add(@NotNull KMessageSingle message) {
        // Add the message's line and actions to this message
        this.line += message.getLine();
        super.addActions(message.getActions());
        return this;
    }

    @NotNull
    public KMessageSingle add(@NotNull String content) {
        // Add the message's line and actions to this message
        this.line += content;
        return this;
    }

    @Override
    public @NotNull List<String> getLines() {
        return new ArrayList<>(List.of(line));
    }

    @NotNull
    public static KMessageSingle ofClickRunCommand(@NotNull String line, @NotNull String command) {
        // Best way to apply an action to the entire string is with a placeholder and
        //  an action that's 'replacement' is the desired contents
        final String placeholder = "{cR_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setClickRunCommand(command));
    }
    @NotNull
    public static KMessageSingle ofClickSuggestCommand(@NotNull String line, @NotNull String suggestion) {
        final String placeholder = "{cS_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setClickSuggestCommand(suggestion));
    }
    @NotNull
    public static KMessageSingle ofClickOpenURL(@NotNull String line, @NotNull String url) {
        final String placeholder = "{cO_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setClickOpenURL(url));
    }
    @NotNull
    public static KMessageSingle ofHoverText(@NotNull String line, @NotNull String text) {
        final String placeholder = "{hT_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setHoverText(text));
    }
    @NotNull
    public static KMessageSingle ofHoverItem(@NotNull String line, @NotNull ItemStack item) {
        final String placeholder = "{hI_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setHoverItem(item));
    }
}
