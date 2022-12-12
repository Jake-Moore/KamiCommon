package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import com.kamikazejamplugins.kamicommon.util.components.actions.Hover;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class HoverItem extends Hover {
    public final @Nullable ItemStack itemStack;

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
    public void addHoverEvent(TextComponent component) {
        if (itemStack == null) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(" ")}));
        }else {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{getItemText(itemStack)}));
        }
    }

    /**
     * Creates a TextComponent that can be used in a HoverEvent from an ItemStack.
     *
     * @param item the ItemStack to be converted to text
     */
    public static TextComponent getItemText(ItemStack item) {
        if (item == null) { return new TextComponent(""); }
        return getInstance(item).getItemText(item);
    }

    private static ItemText getInstance(ItemStack item) {
        String version = NmsManager.getNMSVersion();
        switch (version) {
            case "v1_8_R1":
                return new ItemText_1_8_R1();
            case "v1_8_R2":
                return new ItemText_1_8_R2();
            case "v1_8_R3":
                return new ItemText_1_8_R3();
            case "v1_9_R1":
                return new ItemText_1_9_R1();
            case "v1_9_R2":
                return new ItemText_1_9_R2();
            case "v1_10_R1":
                return new ItemText_1_10_R1();
            case "v1_11_R1":
                return new ItemText_1_11_R1();
            case "v1_12_R1":
                return new ItemText_1_12_R1();
            case "v1_13_R1":
                return new ItemText_1_13_R1();
            case "v1_13_R2":
                return new ItemText_1_13_R2();
            case "v1_14_R1":
                return new ItemText_1_14_R1();
            case "v1_15_R1":
                return new ItemText_1_15_R1();
            case "v1_16_R1":
                return new ItemText_1_16_R1();
            case "v1_16_R2":
                return new ItemText_1_16_R2();
            case "v1_16_R3":
                return new ItemText_1_16_R3();
            case "v1_17_R1":
                return new ItemText_1_17_R1();
            case "v1_18_R1":
                return new ItemText_1_18_R1();
            case "v1_18_R2":
                return new ItemText_1_18_R2();
            case "v1_19_R1":
                return new ItemText_1_19_R1();
            default:
                Bukkit.getLogger().severe("[KamiCommon NBTManager] Unsupported version: " + version);
                return new ItemText_1_8_R1();
        }
    }
}
