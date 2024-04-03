package com.kamikazejam.kamicommon.util.components.actions;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings({"unused"})
public class HoverItem extends Hover {
    private final @Nullable ItemStack itemStack;

    /**
     * Creates a HoverItem object which will only have a hoverEvent for running the command
     *  Use .setClickCommand() or .setClickSuggestion() to chain a clickEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param itemStack The itemstack to show when hovering
     */
    public HoverItem(String placeholder, String replacement, @Nullable ItemStack itemStack) {
        super(placeholder, replacement);
        this.itemStack = itemStack;
    }

    @Override
    public void addHoverEvent(BaseComponent component) {
        if (itemStack == null) {
            this.addHoverText(component, " ");
        }else {
            this.addHoverItem(component, itemStack);
        }
    }
}