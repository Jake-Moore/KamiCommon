package com.kamikazejamplugins.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings({"unused", "UnusedReturnValue", "FieldCanBeLocal", "deprecation"})
public class ItemBuilder {

    private final ItemStack is;
    private String skullOwner;
    private int slot;

    public ItemBuilder(ConfigurationSection section) {
        is = getConfigItem(section);
    }

    public ItemBuilder(ConfigurationSection section, OfflinePlayer offlinePlayer) {
        is = getConfigItem(section, offlinePlayer);
    }

    private static ItemStack getConfigItem(ConfigurationSection config) {
        return getConfigItem(config, null);
    }

    private static ItemStack getConfigItem(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer) {
        Optional<XMaterial> optional = XMaterial.matchXMaterial(config.getString("material"));
        if (optional.isPresent() && optional.get().equals(XMaterial.PLAYER_HEAD)) {
            return getPlayerHead(config, offlinePlayer);
        }
        return getBasicItem(config);
    }

    private static ItemStack getBasicItem(ConfigurationSection config) {
        short damage = (short) config.getInt("damage", 0);
        int amount = config.getInt("amount", 1);

        ItemStack item = new ItemStack(Material.valueOf(config.getString("material")), amount, damage);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(StringUtil.t(config.getString("name")));
        meta.setLore(StringUtil.t(config.getStringList("lore")));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getPlayerHead(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer) {
        ItemStack item = getBasicItem(config);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        SkullMeta skullMeta = (SkullMeta) meta;

        // Set the skull owner if it's not null
        if (offlinePlayer != null) {
            skullMeta.setOwner(offlinePlayer.getName());
        }

        skullMeta.setDisplayName(meta.getDisplayName());
        skullMeta.setLore(meta.getLore());
        item.setItemMeta(skullMeta);
        return item;
    }

    public ItemBuilder(XMaterial m) {
        this(m, 1);
    }

    public ItemBuilder(int id) {
        this(id, 1);
    }

    public ItemBuilder(int id, short damage) {
        this(id, 1, damage);
    }

    public ItemBuilder(XMaterial m, short damage) {
        this(m.getId(), 1, damage);
    }

    public ItemBuilder(int id, int amount) {
        this(id, amount, (short) 0);
    }

    public ItemBuilder(XMaterial m, int amount) {
        this(m, amount, (short) 0);
    }

    public ItemBuilder(int id, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        is = new ItemStack(id, amount, damage);
    }

    public ItemBuilder(XMaterial material, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        assert material.parseMaterial() != null;
        is = new ItemStack(material.parseMaterial(), amount, damage);
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

    public ItemBuilder setUnbreakable(boolean b) {
        if (NmsManager.getFormattedNmsDouble() >= 1.10) {
            try {
                ItemMeta meta = is.getItemMeta();
                Method setUnbreakable = meta.getClass().getDeclaredMethod("setUnbreakable", boolean.class);
                setUnbreakable.setAccessible(true);
                setUnbreakable.invoke(meta, b);
                is.setItemMeta(meta);
            }catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("[KamiCommon ItemBuilder] Error setting unbreakable tag.");
            }

            is.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }else {
            is.getItemMeta().spigot().setUnbreakable(b);
            is.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
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

    public ItemBuilder setType(XMaterial m) {
        is.setType(m.parseMaterial());
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(StringUtil.t(name));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        return setName(name);
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

    public ItemBuilder setNbtString(String key, String s) { NBT.modify(is, nbt -> { nbt.setString(key, s); }); return this; }
    public ItemBuilder setNbtBoolean(String key, boolean b) { NBT.modify(is, nbt -> { nbt.setBoolean(key, b); }); return this; }
    public ItemBuilder setNbtInt(String key, int i) { NBT.modify(is, nbt -> { nbt.setInteger(key, i); }); return this; }
    public ItemBuilder setNbtDouble(String key, double d) { NBT.modify(is, nbt -> { nbt.setDouble(key, d); }); return this; }
    public ItemBuilder setNbtLong(String key, long l) { NBT.modify(is, nbt -> { nbt.setLong(key, l); }); return this; }
    public ItemBuilder setNbtFloat(String key, float f) { NBT.modify(is, nbt -> { nbt.setFloat(key, f); }); return this; }
    public ItemBuilder setNbtShort(String key, short s) { NBT.modify(is, nbt -> { nbt.setShort(key, s); }); return this; }
    public ItemBuilder setNbtByte(String key, byte b) { NBT.modify(is, nbt -> { nbt.setByte(key, b); }); return this; }
    public ItemBuilder setNbtByteArray(String key, byte[] b) { NBT.modify(is, nbt -> { nbt.setByteArray(key, b); }); return this; }
    public ItemBuilder setNbtIntArray(String key, int[] i) { NBT.modify(is, nbt -> { nbt.setIntArray(key, i); }); return this; }
    public ItemBuilder setNbtItemStack(String key, ItemStack i) { NBT.modify(is, nbt -> { nbt.setItemStack(key, i); }); return this; }
    public ItemBuilder setNbtItemStackArray(String key, ItemStack[] i) { NBT.modify(is, nbt -> { nbt.setItemStackArray(key, i); }); return this; }
    public ItemBuilder setNbtUUID(String key, UUID uuid) { NBT.modify(is, nbt -> { nbt.setUUID(key, uuid); }); return this; }


    public ItemBuilder addFlag(ItemFlag flag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(flag);
        is.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return is;
    }

    public ItemStack build() {
        return is;
    }
}
