package com.kamikazejam.kamicommon.actions;

import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.data.ANSI;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter @Accessors(chain = true)
@SuppressWarnings("unused")
public class StandaloneAction {

    private final @NotNull String placeholder;
    private final @NotNull String replacement;

    public @Nullable Click click = null;
    public @Nullable Hover hover = null;

    public StandaloneAction(@NotNull String placeholder, @NotNull String replacement) {
        this.placeholder = placeholder;
        this.replacement = StringUtil.t(replacement);
    }



    /**
     * @param command The command the player runs, when clicked (STARTS WITH '/')
     */
    public StandaloneAction setClickRunCommand(String command) {
        if (!command.startsWith("/")) {
            command = "/" + command;
            System.out.println(ANSI.RED + "StandaloneAction: Command does not start with '/'. Attempting to fix." + ANSI.RESET);
        }
        this.click = new ClickCmd(command);
        return this;
    }
    /**
     * @param suggestion The command/text to suggest to the player, when clicked
     */
    public StandaloneAction setClickSuggestCommand(String suggestion) {
        this.click = new ClickSuggest(suggestion);
        return this;
    }
    /**
     * @param url The url to open, when clicked
     */
    public StandaloneAction setClickOpenURL(String url) {
        this.click = new ClickUrl(url);
        return this;
    }

    /**
     * @param text The text to show when hovering
     */
    public StandaloneAction setHoverText(String text) {
        this.hover = new HoverText(text);
        return this;
    }
}
