package com.kamikazejam.kamicommon.util.components.actions;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// HoverEvent.Action.SHOW_TEXT;
// HoverEvent.Action.SHOW_ITEM;

@Getter
@SuppressWarnings("unused")
public abstract class Hover extends Action {
    private Click clickAction = null;
    public Hover(@NotNull String placeholder, @NotNull String replacement) {
        super(placeholder, replacement);
    }

    public abstract void addHoverEvent(BaseComponent component);



    // Using versioning because at some point the HoverEvent starting taking a Content object, instead of String
    public final void addHoverText(BaseComponent component, String text) {
        component.setHoverEvent(NmsAPI.getHoverEvent().createHoverEvent(NmsAPI.getItemText(), component, text));
    }
    public final void addHoverItem(@NotNull BaseComponent component, @NotNull ItemStack item) {
        component.setHoverEvent(NmsAPI.getHoverEvent().createHoverEvent(NmsAPI.getItemText(), component, item));
    }

    // ------------------------------------------------------------------------------------------ //
    //                                       API METHODS                                          //
    // ------------------------------------------------------------------------------------------ //
    public Hover setClickCommand(String cmd) {
        this.clickAction = new ClickCmd("", "", cmd);
        return this;
    }

    public Hover setClickSuggestion(String suggestion) {
        this.clickAction = new ClickSuggest("", "", suggestion);
        return this;
    }

    public Hover setClickUrl(String url) {
        this.clickAction = new ClickUrl("", "", url);
        return this;
    }

    public void addClickEvent(BaseComponent component) {
        if (clickAction == null) { return; }
        clickAction.addClickEvent(component);
    }
}
