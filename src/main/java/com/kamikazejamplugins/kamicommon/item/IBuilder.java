package com.kamikazejamplugins.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings({"unused", "UnusedReturnValue", "FieldCanBeLocal", "deprecation"})
public abstract class IBuilder {

    final ItemStack is;
    private String skullOwner;
    private int slot;

    public IBuilder(ConfigurationSection section) {
        is = getConfigItem(section, null);
    }
    
    public IBuilder(ConfigurationSection section, OfflinePlayer offlinePlayer) {
        is = getConfigItem(section, offlinePlayer);
    }

    /**
     * @return The item the builder has been building
     */
    public ItemStack build() {
        return is;
    }
    
    public ItemStack getConfigItem(ConfigurationSection config) {
        return getConfigItem(config, null);
    }

    public ItemStack getConfigItem(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer) {
        Optional<XMaterial> optional = XMaterial.matchXMaterial(config.getString("material"));
        if (optional.isPresent() && optional.get().equals(XMaterial.PLAYER_HEAD)) {
            return getPlayerHead(config, offlinePlayer);
        }
        return getBasicItem(config);
    }


    public IBuilder(XMaterial m) {
        this(m, 1);
    }

    public IBuilder(int id) {
        this(id, 1);
    }

    public IBuilder(int id, short damage) {
        this(id, 1, damage);
    }

    public IBuilder(XMaterial m, short damage) {
        this(m.getId(), 1, damage);
    }

    public IBuilder(int id, int amount) {
        this(id, amount, (short) 0);
    }

    public IBuilder(XMaterial m, int amount) {
        this(m, amount, (short) 0);
    }

    public IBuilder(int id, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        is = new ItemStack(id, amount, damage);
    }

    public IBuilder(XMaterial material, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        assert material.parseMaterial() != null;
        is = new ItemStack(material.parseMaterial(), amount, damage);
    }

    public IBuilder(ItemStack is) {
        this(is, true);
    }

    public IBuilder(ItemStack is, boolean clone) {
        if (clone) {
            this.is = is.clone();
        } else {
            this.is = is;
        }
    }

    public IBuilder setUnbreakable(boolean b) {
        if (NmsManager.getFormattedNmsDouble() >= 1.10) {
            try {
                ItemMeta meta = is.getItemMeta();
                Method setUnbreakable = meta.getClass().getDeclaredMethod("setUnbreakable", boolean.class);
                setUnbreakable.setAccessible(true);
                setUnbreakable.invoke(meta, b);
                is.setItemMeta(meta);
            }catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("[KamiCommon IBuilder] Error setting unbreakable tag.");
            }

            is.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }else {
            is.getItemMeta().spigot().setUnbreakable(b);
            is.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        return this;
    }

    public IBuilder setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public IBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public IBuilder setDurability(int dur) {
        is.setDurability((short) dur);
        return this;
    }

    public IBuilder setType(XMaterial m) {
        is.setType(m.parseMaterial());
        return this;
    }

    public IBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(StringUtil.t(name));
        is.setItemMeta(im);
        return this;
    }

    public IBuilder setDisplayName(String name) {
        return setName(name);
    }

    public IBuilder replaceName(String find, String replacement) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(StringUtil.t(im.getDisplayName().replace(find, replacement)));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Searches for the find placeholder in the lore, and replaces that entire line with replacement
     * @param find The string to search for in the lore
     * @param replacement The lines to swap in, in place of the entire line containing find
     * @return The IBuilder with replaced lore
     */
    public IBuilder replaceLoreLine(String find, List<String> replacement) {
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

    public IBuilder replaceLore(String find, String replacement) {
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

    public IBuilder replaceBoth(String find, String replacement) {
        replaceName(find, replacement);
        replaceLore(find, replacement);
        return this;
    }

    public IBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public List<String> getLore() {
        return is.getItemMeta().getLore();
    }

    public IBuilder removeLore() {
        setLore(new ArrayList<>());
        return this;
    }

    public IBuilder hideAttributes() {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(im);
        return this;
    }

    public IBuilder addGlow() {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(im);
        return this;
    }

    public IBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public IBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    public IBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    public IBuilder setLore(String... line) {
        return setLore(Arrays.asList(line));
    }

    public IBuilder setLore(List<String> lore) {
        lore = StringUtil.t(lore);
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public IBuilder addLoreLines(List<String> line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) {
            lore = new ArrayList<>(im.getLore());
        }
        lore.addAll(StringUtil.t(line));
        return setLore(lore);
    }

    public IBuilder addLoreLines(String... line) {
        return addLoreLines(Arrays.asList(line));
    }

    public IBuilder setNbtString(String key, String s) { NBT.modify(is, nbt -> { nbt.setString(key, s); }); return this; }
    public IBuilder setNbtBoolean(String key, boolean b) { NBT.modify(is, nbt -> { nbt.setBoolean(key, b); }); return this; }
    public IBuilder setNbtInt(String key, int i) { NBT.modify(is, nbt -> { nbt.setInteger(key, i); }); return this; }
    public IBuilder setNbtDouble(String key, double d) { NBT.modify(is, nbt -> { nbt.setDouble(key, d); }); return this; }
    public IBuilder setNbtLong(String key, long l) { NBT.modify(is, nbt -> { nbt.setLong(key, l); }); return this; }
    public IBuilder setNbtFloat(String key, float f) { NBT.modify(is, nbt -> { nbt.setFloat(key, f); }); return this; }
    public IBuilder setNbtShort(String key, short s) { NBT.modify(is, nbt -> { nbt.setShort(key, s); }); return this; }
    public IBuilder setNbtByte(String key, byte b) { NBT.modify(is, nbt -> { nbt.setByte(key, b); }); return this; }
    public IBuilder setNbtByteArray(String key, byte[] b) { NBT.modify(is, nbt -> { nbt.setByteArray(key, b); }); return this; }
    public IBuilder setNbtIntArray(String key, int[] i) { NBT.modify(is, nbt -> { nbt.setIntArray(key, i); }); return this; }
    public IBuilder setNbtItemStack(String key, ItemStack i) { NBT.modify(is, nbt -> { nbt.setItemStack(key, i); }); return this; }
    public IBuilder setNbtItemStackArray(String key, ItemStack[] i) { NBT.modify(is, nbt -> { nbt.setItemStackArray(key, i); }); return this; }
    public IBuilder setNbtUUID(String key, UUID uuid) { NBT.modify(is, nbt -> { nbt.setUUID(key, uuid); }); return this; }


    public IBuilder addFlag(ItemFlag flag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(flag);
        is.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return is;
    }










    /**
     * @return A base ItemStack from the config
     */
    public abstract ItemStack getBasicItem(ConfigurationSection config);

    /**
     * @return A player head ItemStack from the config, using offlinePlayer's skin
     */
    public abstract ItemStack getPlayerHead(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer);

    /**
     * @return A clone of this item `new IBuilder(this.is)`
     */
    public abstract IBuilder clone();
}
