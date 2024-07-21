package com.kamikazejam.kamicommon.nms.abstraction.chat;

import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class KMessage {
    private final @NotNull List<Action> actions = new ArrayList<>();
    @Setter
    private boolean translate = true;

    public KMessage() {}
    public KMessage(@NotNull Action... actions) {
        this.actions.addAll(Arrays.asList(actions));
    }
    public KMessage(@NotNull List<Action> actions) {
        this.actions.addAll(actions);
    }

    @NotNull
    public abstract List<String> getLines();

    @NotNull
    public final KMessage addAction(@NotNull Action action) {
        actions.add(action);
        return this;
    }

    @NotNull
    public final KMessage addActions(@NotNull List<Action> actions) {
        this.actions.addAll(actions);
        return this;
    }

    // Mirror Methods from Action
    @NotNull
    public final KMessage addHoverItem(@NotNull String placeholder, @NotNull String replacement, @NotNull ItemStack item) {
        return this.addAction(new Action(placeholder, replacement).setHoverItem(item));
    }
    @NotNull
    public final KMessage addClickRunCommand(@NotNull String placeholder, @NotNull String replacement, @NotNull String command) {
        return this.addAction(new Action(placeholder, replacement).setClickRunCommand(command));
    }
    @NotNull
    public final KMessage addClickSuggestCommand(@NotNull String placeholder, @NotNull String replacement, @NotNull String suggestion) {
        return this.addAction(new Action(placeholder, replacement).setClickSuggestCommand(suggestion));
    }
    @NotNull
    public final KMessage addClickOpenURL(@NotNull String placeholder, @NotNull String replacement, @NotNull String url) {
        return this.addAction(new Action(placeholder, replacement).setClickOpenURL(url));
    }
    @NotNull
    public final KMessage addHoverText(@NotNull String placeholder, @NotNull String replacement, @NotNull String text) {
        return this.addAction(new Action(placeholder, replacement).setHoverText(text));
    }
}
