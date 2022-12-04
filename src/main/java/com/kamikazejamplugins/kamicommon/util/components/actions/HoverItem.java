package com.kamikazejamplugins.kamicommon.util.components.actions;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class HoverItem extends Hover {
    public final ItemStack itemStack;

    /**
     * Creates a HoverItem object which will only have a hoverEvent for running the command
     *  Use .setClickCommand() or .setClickSuggestion() to chain a clickEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param itemStack The itemstack to show when hovering
     */
    public HoverItem(String placeholder, String replacement, ItemStack itemStack) {
        super(placeholder, replacement);
        this.itemStack = itemStack;
    }

    @Override
    public void addHoverEvent(TextComponent component) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{getItemText(itemStack)}));
    }

    /**
     * Creates a TextComponent that can be used in a HoverEvent from an ItemStack.
     *
     * @param item the ItemStack to be converted to text
     */
    public static TextComponent getItemText(org.bukkit.inventory.ItemStack item) {
        if (item == null) { return new TextComponent(""); }
        return getItemText(CraftItemStack.asNMSCopy(item));
    }

    private static TextComponent getItemText(net.minecraft.server.v1_8_R3.ItemStack item) {
        if (item == null) { return new TextComponent(""); }
        return new TextComponent(item.save(new NBTTagCompound()).toString());
    }
}
