package com.kamikazejam.kamicommon.nms.abstraction.chat.impl;

import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * A block of lines that can be sent to a player.
 */
@Setter @Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class KMessageBlock extends KMessage {
    private @NotNull List<String> lines = new ArrayList<>();

    public KMessageBlock(@NotNull String line) {
        super();
        this.lines.add(line);
    }
    public KMessageBlock(@NotNull String... lines) {
        super();
        this.lines.add(Arrays.toString(lines));
    }
    public KMessageBlock(@NotNull List<String> lines) {
        super();
        this.lines.addAll(lines);
    }
    public KMessageBlock(@NotNull String line, @NotNull Action... actions) {
        super(actions);
        this.lines.add(line);
    }
    public KMessageBlock(@NotNull String line, @NotNull List<Action> actions) {
        super(actions);
        this.lines.add(line);
    }
    public KMessageBlock(@NotNull List<String> lines, @NotNull Action... actions) {
        super(actions);
        this.lines.addAll(lines);
    }
    public KMessageBlock(@NotNull List<String> lines, @NotNull List<Action> actions) {
        super(actions);
        this.lines.addAll(lines);
    }

    @NotNull
    public KMessageBlock addLine(@NotNull String line) {
        lines.add(line);
        return this;
    }

    @NotNull
    public static KMessageBlock ofClickRunCommand(@NotNull String line, @NotNull String command) {
        // Best way to apply an action to the entire string is with a placeholder and
        //  an action that's 'replacement' is the desired contents
        final String placeholder = "{cR_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setClickRunCommand(command));
    }
    @NotNull
    public static KMessageBlock ofClickSuggestCommand(@NotNull String line, @NotNull String suggestion) {
        final String placeholder = "{cS_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setClickSuggestCommand(suggestion));
    }
    @NotNull
    public static KMessageBlock ofClickOpenURL(@NotNull String line, @NotNull String url) {
        final String placeholder = "{cO_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setClickOpenURL(url));
    }
    @NotNull
    public static KMessageBlock ofHoverText(@NotNull String line, @NotNull String text) {
        final String placeholder = "{hT_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setHoverText(text));
    }
    @NotNull
    public static KMessageBlock ofHoverItem(@NotNull String line, @NotNull ItemStack item) {
        final String placeholder = "{hI_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setHoverItem(item));
    }
}
