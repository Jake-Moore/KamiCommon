package com.kamikazejamplugins.kamicommon.util.components.actions;

import com.kamikazejamplugins.kamicommon.nms.NmsManager;
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
    public static TextComponent getItemText(org.bukkit.inventory.ItemStack item) {
        if (item == null) { return new TextComponent(""); }

        String version = NmsManager.getNMSVersion();
        switch (version) {
            case "v1_8_R1":
                net.minecraft.server.v1_8_R1.ItemStack v1_8_R1Stack = org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_8_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_8_R1Stack.save(new net.minecraft.server.v1_8_R1.NBTTagCompound()).toString());
            case "v1_8_R2":
                net.minecraft.server.v1_8_R2.ItemStack v1_8_R2Stack = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_8_R2Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_8_R2Stack.save(new net.minecraft.server.v1_8_R2.NBTTagCompound()).toString());
            case "v1_8_R3":
                net.minecraft.server.v1_8_R3.ItemStack v1_8_R3Stack = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_8_R3Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_8_R3Stack.save(new net.minecraft.server.v1_8_R3.NBTTagCompound()).toString());
            case "v1_9_R1":
                net.minecraft.server.v1_9_R1.ItemStack v1_9_R1Stack = org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_9_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_9_R1Stack.save(new net.minecraft.server.v1_9_R1.NBTTagCompound()).toString());
            case "v1_9_R2":
                net.minecraft.server.v1_9_R2.ItemStack v1_9_R2Stack = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_9_R2Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_9_R2Stack.save(new net.minecraft.server.v1_9_R2.NBTTagCompound()).toString());
            case "v1_10_R1":
                net.minecraft.server.v1_10_R1.ItemStack v1_10_R1Stack = org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_10_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_10_R1Stack.save(new net.minecraft.server.v1_10_R1.NBTTagCompound()).toString());
            case "v1_11_R1":
                net.minecraft.server.v1_11_R1.ItemStack v1_11_R1Stack = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_11_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_11_R1Stack.save(new net.minecraft.server.v1_11_R1.NBTTagCompound()).toString());
            case "v1_12_R1":
                net.minecraft.server.v1_12_R1.ItemStack v1_12_R1Stack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_12_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_12_R1Stack.save(new net.minecraft.server.v1_12_R1.NBTTagCompound()).toString());
            case "v1_13_R1":
                net.minecraft.server.v1_13_R1.ItemStack v1_13_R1Stack = org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_13_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_13_R1Stack.save(new net.minecraft.server.v1_13_R1.NBTTagCompound()).toString());
            case "v1_13_R2":
                net.minecraft.server.v1_13_R2.ItemStack v1_13_R2Stack = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_13_R2Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_13_R2Stack.save(new net.minecraft.server.v1_13_R2.NBTTagCompound()).toString());
            case "v1_14_R1":
                net.minecraft.server.v1_14_R1.ItemStack v1_14_R1Stack = org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_14_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_14_R1Stack.save(new net.minecraft.server.v1_14_R1.NBTTagCompound()).toString());
            case "v1_15_R1":
                net.minecraft.server.v1_15_R1.ItemStack v1_15_R1Stack = org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_15_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_15_R1Stack.save(new net.minecraft.server.v1_15_R1.NBTTagCompound()).toString());
            case "v1_16_R1":
                net.minecraft.server.v1_16_R1.ItemStack v1_16_R1Stack = org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_16_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_16_R1Stack.save(new net.minecraft.server.v1_16_R1.NBTTagCompound()).toString());
            case "v1_16_R2":
                net.minecraft.server.v1_16_R2.ItemStack v1_16_R2Stack = org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_16_R2Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_16_R2Stack.save(new net.minecraft.server.v1_16_R2.NBTTagCompound()).toString());
            case "v1_16_R3":
                net.minecraft.server.v1_16_R3.ItemStack v1_16_R3Stack = org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_16_R3Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_16_R3Stack.save(new net.minecraft.server.v1_16_R3.NBTTagCompound()).toString());
            case "v1_17_R1":
                net.minecraft.world.item.ItemStack v1_17_R1Stack = org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_17_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_17_R1Stack.save(new net.minecraft.nbt.CompoundTag()).toString());
            case "v1_18_R1":
                net.minecraft.world.item.ItemStack v1_18_R1Stack = org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_18_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_18_R1Stack.save(new net.minecraft.nbt.CompoundTag()).toString());
            case "v1_18_R2":
                net.minecraft.world.item.ItemStack v1_18_R2Stack = org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_18_R2Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_18_R2Stack.save(new net.minecraft.nbt.CompoundTag()).toString());
            case "v1_19_R1":
                net.minecraft.world.item.ItemStack v1_19_R1Stack = org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack.asNMSCopy(item);
                if (v1_19_R1Stack == null) { return new TextComponent(""); }
                return new TextComponent(v1_19_R1Stack.save(new net.minecraft.nbt.CompoundTag()).toString());
            default:
                Bukkit.getLogger().severe("[KamiCommon NBTManager] Unsupported version: " + version);
                return new TextComponent("");
        }
    }
}
