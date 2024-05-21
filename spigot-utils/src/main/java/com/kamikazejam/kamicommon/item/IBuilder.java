package com.kamikazejam.kamicommon.item;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.StringUtilP;
import com.kamikazejam.kamicommon.util.data.TriState;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class IBuilder {

    @Setter private @Nullable ItemStack base = null;
    private @NotNull XMaterial material = XMaterial.AIR;

    private int amount = 1;
    private short damage = 0;

    // Name & Lore can be null if base is set
    private @Nullable String name = " ";
    private @Nullable List<String> lore = new ArrayList<>();

    private @NotNull TriState unbreakable = TriState.NOT_SET;
    private final @NotNull List<ItemFlag> itemFlags = new ArrayList<>();
    private final @NotNull Map<Enchantment, Integer> enchantments = new HashMap<>();
    private boolean addGlow = false;

    @Setter
    private @Nullable String skullOwner = null; // player name

    public IBuilder() {}

    public IBuilder(ConfigurationSection section) {
        loadConfigItem(section, null, true);
    }
    public IBuilder(ConfigurationSection section, OfflinePlayer offlinePlayer) {
        loadConfigItem(section, offlinePlayer, true);
    }
    public IBuilder(@NotNull XMaterial material, ConfigurationSection section) {
        this.material = material;
        loadConfigItem(section, null, false);
    }
    public IBuilder(@NotNull XMaterial material, ConfigurationSection section, OfflinePlayer offlinePlayer) {
        this.material = material;
        loadConfigItem(section, offlinePlayer, false);
    }
    public IBuilder(@Nullable ItemStack base, ConfigurationSection section) {
        this.base = base;
        loadConfigItem(section, null, false);
    }
    public IBuilder(@Nullable ItemStack base, ConfigurationSection section, OfflinePlayer offlinePlayer) {
        this.base = base;
        loadConfigItem(section, offlinePlayer, false);
    }

    /**
     * @return The item the builder has been building
     */
    public ItemStack build() {
        return build(null);
    }

    /**
     * @return The item the builder has been building
     * @param player The player to build the item for (for placeholders)
     */
    public ItemStack build(@Nullable Player player) {
        return this.build(player, 0);
    }

    /**
     * @return The item the builder has been building
     * @param player The player to build the item for (for placeholders)
     * @param materialIndex The index of the material to use in the materials list
     */
    public ItemStack build(@Nullable Player player, int materialIndex) {
        if (material == XMaterial.AIR && base == null) { return new ItemStack(Material.AIR); }

        final ItemStack itemStack;
        if (base != null) {
            itemStack = base;
        }else {
            assert material.parseMaterial() != null;
            if (damage != 0) {
                itemStack = new ItemStack(material.parseMaterial(), amount, damage);
            } else {
                itemStack = material.parseItem();
                assert itemStack != null;
                itemStack.setAmount(amount);
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) { return itemStack; }

        // Skull Meta
        if (skullOwner != null && meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwner(skullOwner);
        }

        // Name and lore
        if (name != null) { meta.setDisplayName(StringUtilP.p(player, name)); }
        if (lore != null) { meta.setLore(StringUtilP.p(player, lore)); }

        // Unbreakable
        if (unbreakable != TriState.NOT_SET) {
            meta = NmsAPI.getItemEditor().setUnbreakable(meta, unbreakable.toBoolean());
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        // Flags
        if (!itemFlags.isEmpty()) {
            for (ItemFlag itemFlag : itemFlags) {
                if (!meta.getItemFlags().contains(itemFlag)) { meta.addItemFlags(itemFlag); }
            }
        }

        // Glow
        if (addGlow) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            if (!meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) { meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); }
        }

        // Enchantments
        if (!enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * @param config The configuration section to load data from
     * @param offlinePlayer The player to use as the skull owner
     */
    private void loadConfigItem(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer, boolean loadMaterial) {
        if (offlinePlayer != null) {
            this.skullOwner = offlinePlayer.getName();
        }
        loadBasicItem(config, loadMaterial);
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
        this(m, 1, damage);
    }

    public IBuilder(XMaterial m, int amount) {
        this(m, amount, (short) 0);
    }

    public IBuilder(int id, int amount) { this(id, amount, (short) 0); }

    @SuppressWarnings("deprecation")
    public IBuilder(int id, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        XMaterial.matchXMaterial(id, (byte) damage).ifPresent(xMaterial -> material = xMaterial);
        this.amount = amount;
        this.damage = damage;
    }

    public IBuilder(@NotNull XMaterial material, int amount, short damage) {
        if (amount > 64) { amount = 64; }
        assert material.parseMaterial() != null;
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    public IBuilder(@NotNull ItemStack is) {
        this(is, true);
    }

    public IBuilder(@NotNull ItemStack is, boolean clone) {
        // Erase default name and lore, so that the base isn't overwritten
        this.name = null;
        this.lore = null;

        is = (clone) ? is.clone() : is;
        this.base = is;
    }

    public IBuilder setUnbreakable(boolean b) {
        unbreakable = TriState.byBoolean(b);
        return this;
    }

    public IBuilder setDurability(short dur) {
        this.damage = dur;
        return this;
    }

    public IBuilder setDurability(int dur) { return setDurability((short) dur); }

    public IBuilder setType(XMaterial m) {
        this.material = m;
        return this;
    }

    public IBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public IBuilder setDisplayName(String name) {
        return setName(name);
    }

    public IBuilder setMaterial(XMaterial material) {
        this.material = material;
        return this;
    }
    public IBuilder setMaterial(Material material) {
        return setMaterial(XMaterial.matchXMaterial(material));
    }

    public IBuilder replaceName(String find, String replacement) {
        assert name != null;
        name = name.replaceAll(Pattern.quote(find), replacement);
        return this;
    }

    /**
     * Searches for the find placeholder in the lore, and replaces that entire line with replacement
     * @param find The string to search for in the lore
     * @param replacement The lines to swap in, in place of the entire line containing find
     * @return The IBuilder with replaced lore
     */
    public IBuilder replaceLoreLine(String find, List<String> replacement) {
        final List<String> newLore = new ArrayList<>();
        assert lore != null;
        for (String s : lore) {
            if (ChatColor.stripColor(s).contains(ChatColor.stripColor(find))) {
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
        assert lore != null;
        for (String s : lore) {
            if (s.contains(find)) {
                newLore.add(s.replaceAll(Pattern.quote(find), replacement));
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
        this.amount = amount;
        return this;
    }

    public IBuilder removeLore() {
        setLore(new ArrayList<>());
        return this;
    }

    public IBuilder hideAttributes() {
        if (!itemFlags.contains(ItemFlag.HIDE_ATTRIBUTES)) { itemFlags.add(ItemFlag.HIDE_ATTRIBUTES); }
        if (!itemFlags.contains(ItemFlag.HIDE_ENCHANTS)) { itemFlags.add(ItemFlag.HIDE_ENCHANTS); }
        if (!itemFlags.contains(ItemFlag.HIDE_PLACED_ON)) { itemFlags.add(ItemFlag.HIDE_PLACED_ON); }
        if (!itemFlags.contains(ItemFlag.HIDE_UNBREAKABLE)) { itemFlags.add(ItemFlag.HIDE_UNBREAKABLE); }
        if (!itemFlags.contains(ItemFlag.HIDE_POTION_EFFECTS)) { itemFlags.add(ItemFlag.HIDE_POTION_EFFECTS); }
        return this;
    }

    public IBuilder addGlow() {
        this.addGlow = true;
        return this;
    }

    public IBuilder removeEnchantment(Enchantment enchant) {
        this.enchantments.remove(enchant);
        return this;
    }

    public IBuilder addEnchant(Enchantment enchant, int level) {
        this.enchantments.put(enchant, level);
        return this;
    }

    public IBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    public IBuilder setLore(String... line) {
        return setLore(Arrays.asList(line));
    }

    public IBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public IBuilder addLoreLines(List<String> lines) {
        if (lore == null) { lore = new ArrayList<>(); }
        lore.addAll(lines);
        return this;
    }

    public IBuilder addLoreLines(String... line) {
        return addLoreLines(Arrays.asList(line));
    }

    public IBuilder addFlag(ItemFlag flag) {
        if (!itemFlags.contains(flag)) { itemFlags.add(flag); }
        return this;
    }

    public ItemStack toItemStack() { return build(); }
    public ItemStack toItemStack(Player player) { return build(player); }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean loadMaterial(ConfigurationSection section) {
        if (section.isString("material")) {
            @Nullable XMaterial mat = parseMaterial(section.getString("material"));
            if (mat != null) {
                this.material = mat;
                return true;
            }
        }
        if (section.isString("type")) {
            @Nullable XMaterial mat = parseMaterial(section.getString("type"));
            if (mat != null) {
                this.material = mat;
                return true;
            }
        }
        return false;
    }

    /**
     * Nullable because the material string may be another IBuilder format (like ItemsAdder namespacedID)
     */
    public @Nullable XMaterial parseMaterial(String mat) {
        return XMaterial.matchXMaterial(mat).orElse(null);
    }

    public abstract void loadBasicItem(ConfigurationSection config, boolean loadMaterial);

    public abstract IBuilder clone();

    public IBuilder loadClone(IBuilder builder) {
        // Basics
        builder.setMaterial(material);
        builder.setAmount(amount);
        builder.setDurability(damage);

        // Meta
        builder.name = name;
        builder.lore = new ArrayList<>();
        if (lore != null) { builder.lore.addAll(lore); }

        // Additional Flags
        builder.unbreakable = unbreakable;
        builder.itemFlags.addAll(itemFlags);
        builder.enchantments.putAll(enchantments);
        builder.addGlow = addGlow;
        builder.skullOwner = skullOwner;
        if (base != null) {
            builder.base = base.clone();
        }
        return builder;
    }
}
