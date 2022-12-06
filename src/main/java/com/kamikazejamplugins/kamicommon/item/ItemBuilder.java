package com.kamikazejamplugins.kamicommon.item;

import com.kamikazejamplugins.kamicommon.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue", "FieldCanBeLocal", "deprecation"})
public class ItemBuilder {

    private final ItemStack is;
    private String skullOwner;
    private int slot;

    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(int id) {
        this(id, 1);
    }

    public ItemBuilder(int id, short damage) {
        this(id, 1, damage);
    }

    public ItemBuilder(Material m, short damage) {
        this(m.getId(), 1, damage);
    }

    public ItemBuilder(int id, int amount) {
        this(id, amount, (short) 0);
    }

    public ItemBuilder(Material m, int amount) {
        this(m, amount, (short) 0);
    }

    public ItemBuilder(int id, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        is = new ItemStack(id, amount, damage);
    }

    public ItemBuilder(Material material, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        is = new ItemStack(material, amount, damage);
    }

    public ItemBuilder(ItemStack is) {
        this(is, true);
    }

    public ItemBuilder(ItemStack is, boolean clone) {
        if (clone) {
            this.is = is.clone();
        } else {
            this.is = is;
        }
    }

    private ItemBuilder setUnbreakable(boolean b) {
        is.getItemMeta().spigot().setUnbreakable(b);
        is.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public ItemBuilder setDurability(int dur) {
        is.setDurability((short) dur);
        return this;
    }

    public ItemBuilder setType(Material m) {
        is.setType(m);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(StringUtil.t(name));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder replaceName(String find, String replacement) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(StringUtil.t(im.getDisplayName().replace(find, replacement)));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Searches for the find placeholder in the lore, and replaces that entire line with replacement
     * @param find The string to search for in the lore
     * @param replacement The lines to swap in, in place of the entire line containing find
     * @return The ItemBuilder with replaced lore
     */
    public ItemBuilder replaceLoreLine(String find, List<String> replacement) {
        final List<String> lore = getLore();
        final List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            if(ChatColor.stripColor(s).contains(find)) {
                newLore.addAll(replacement);
            } else {
                newLore.add(s);
            }
        }
        setLore(newLore);
        return this;
    }

    public ItemBuilder replaceLore(String find, String replacement) {
        final List<String> newLore = new ArrayList<>();
        for (String s : getLore()) {
            if (s.contains(find)) {
                newLore.add(s.replace(find, replacement));
            }else {
                newLore.add(s);
            }
        }
        setLore(newLore);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public List<String> getLore() {
        return is.getItemMeta().getLore();
    }

    public ItemBuilder removeLore() {
        setLore(new ArrayList<>());
        return this;
    }

    public ItemBuilder hideAttributes() {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addGlow() {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setLore(String... line) {
        return setLore(Arrays.asList(line));
    }

    public ItemBuilder setLore(List<String> lore) {
        lore = StringUtil.t(lore);
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLines(List<String> line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) {
            lore = new ArrayList<>(im.getLore());
        }
        lore.addAll(StringUtil.t(line));
        return setLore(lore);
    }

    public ItemBuilder addLoreLines(String... line) {
        return addLoreLines(Arrays.asList(line));
    }
    public ItemStack toItemStack() {
        return is;
    }
}
