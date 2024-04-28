package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Action extends StandaloneAction {
    public Action(@NotNull String placeholder, @NotNull String replacement) {
        super(placeholder, replacement);
    }

    /**
     * @param item The ItemStack to show when hovering
     */
    public Action setHoverItem(ItemStack item) {
        this.hover = new HoverItem(item);
        return this;
    }

    /**
     * @param command The command the player runs, when clicked (STARTS WITH '/')
     */
    @Override
    public Action setClickRunCommand(String command) {
        super.setClickRunCommand(command);
        return this;
    }
    /**
     * @param suggestion The command/text to suggest to the player, when clicked
     */
    @Override
    public Action setClickSuggestCommand(String suggestion) {
        super.setClickSuggestCommand(suggestion);
        return this;
    }
    /**
     * @param url The url to open, when clicked
     */
    @Override
    public Action setClickOpenURL(String url) {
        super.setClickOpenURL(url);
        return this;
    }

    /**
     * @param text The text to show when hovering
     */
    @Override
    public Action setHoverText(String text) {
        super.setHoverText(text);
        return this;
    }
}
