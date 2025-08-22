package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.StringUtilP;
import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.util.data.TriState;
import com.kamikazejam.kamicommon.util.nms.MaterialFlatteningUtil;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final @NotNull Map<XEnchantment, Integer> enchantments = new HashMap<>();
    private final @NotNull Map<String, Pair<NbtType, Object>> nbtData = new HashMap<>();
    private boolean addGlow = false;

    @Setter
    private @Nullable String skullOwner = null; // player name

    public IBuilder() {}

    public IBuilder(@NotNull ConfigurationSection section) {
        loadConfigItem(section, null, true);
    }
    public IBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        loadConfigItem(section, offlinePlayer, true);
    }
    public IBuilder(@NotNull XMaterial material, @NotNull ConfigurationSection section) {
        this.material = material;
        this.damage = material.getData();
        loadConfigItem(section, null, false);
    }
    public IBuilder(@NotNull XMaterial material, @NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        this.material = material;
        this.damage = material.getData();
        loadConfigItem(section, offlinePlayer, false);
    }
    public IBuilder(@Nullable ItemStack base, ConfigurationSection section) {
        loadBase(base);
        loadConfigItem(section, null, false);
    }
    public IBuilder(@Nullable ItemStack base, ConfigurationSection section, OfflinePlayer offlinePlayer) {
        loadBase(base);
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
            itemStack = material.parseItem();
            // Update the ItemStack amount
            assert itemStack != null;
            itemStack.setAmount(amount);
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
            meta.addEnchant(XEnchantment.INFINITY.getEnchant(), 1, true);
            if (!meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) { meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); }
        }

        // Enchantments
        if (!enchantments.isEmpty()) {
            for (Map.Entry<XEnchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey().getEnchant(), entry.getValue(), true);
            }
        }

        // Apply meta
        itemStack.setItemMeta(meta);

        // Load NBT
        if (!nbtData.isEmpty()) {
            NBT.modify(itemStack, nbt -> {
                for (Map.Entry<String, Pair<NbtType, Object>> entry : nbtData.entrySet()) {
                    String nbtKey = entry.getKey();
                    Pair<NbtType, Object> pair = entry.getValue();
                    pair.getA().write(nbt, nbtKey, pair.getB());
                }
            });
        }
        return itemStack;
    }

    final void loadBase(@Nullable ItemStack base) {
        this.base = base;
        if (base == null) { return; }
        // Copy the base's ItemMeta so it doesn't get lost
        // Specifically name and lore, if other properties are set they will be overwritten
        // But by default lore is an empty list and needs to be set
        @Nullable ItemMeta meta = base.getItemMeta();
        if (meta != null) {
            this.name = meta.getDisplayName();
            this.lore = meta.getLore();
        }else {
            this.name = null;
            this.lore = null;
        }
    }

    /**
     * @param config The configuration section to load data from
     * @param offlinePlayer The player to use as the skull owner
     */
    final void loadConfigItem(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer, boolean loadMaterial) {
        if (offlinePlayer != null) {
            this.skullOwner = offlinePlayer.getName();
        }
        // Load Damage, so it's available in the Type Search
        this.setDurability(config.getInt("damage", 0));

        // Load Types, which is complicated due to the 1.13 flattening
        if (loadMaterial) {
            loadTypes(config);
        }

        // Load basic values from config
        this.setAmount(config.getInt("amount", 1));
        this.setName(config.getString("name"));
        this.setLore(config.getStringList("lore"));
        // Load Enchantments
        this.loadEnchantments(config);
        // Load Misc properties
        if (config.getBoolean("glow") || config.getBoolean("addGlow")) {
            this.addGlow = true;
        }

        // Load NBT
        this.loadNBT(config);
    }

    final void loadEnchantments(ConfigurationSection config) {
        this.enchantments.clear();
        if (!config.isConfigurationSection("enchantments")) { return; }

        // Load the sub-keys
        for (String key : config.getConfigurationSection("enchantments").getKeys(false)) {
            XEnchantment enchant = XEnchantment.matchXEnchantment(key).orElseThrow();
            int level = config.getInt("enchantments." + key);
            if (level <= 0) {
                throw new IllegalArgumentException("Invalid enchantment level: " + level);
            }
            this.enchantments.put(enchant, level);
        }
    }

    final void loadNBT(ConfigurationSection config) {
        this.nbtData.clear();
        if (!config.isConfigurationSection("nbt")) { return; }
        ConfigurationSection section = config.getConfigurationSection("nbt");

        // Load the sub-keys
        for (String key : section.getKeys(false)) {
            String nbtKey = section.getString(key + ".key");
            NbtType type = NbtType.fromName(section.getString(key + ".type"));
            Object value = type.readConf(section, key + ".value");
            this.nbtData.put(nbtKey, Pair.of(type, value));
        }
    }

    public IBuilder(XMaterial m) {
        this(m, 1);
    }

    public IBuilder(XMaterial m, short damage) {
        this(m, 1, damage);
    }

    public IBuilder(XMaterial m, int amount) {
        this(m, amount, (short) 0);
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
        loadBase((clone) ? is.clone() : is);
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
        if (name == null) { return this; }
        name = name.replace(find, replacement);
        return this;
    }

    public IBuilder replaceNamePAPI() {
        return replaceNamePAPI(null);
    }
    public IBuilder replaceNamePAPI(@Nullable OfflinePlayer player) {
        if (name == null) { return this; }
        name = StringUtilP.p(player, name);
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
        if (lore == null) { return this; }
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
        if (lore == null) { return this; }
        for (String s : lore) {
            if (s.contains(find)) {
                newLore.add(s.replace(find, replacement));
            }else {
                newLore.add(s);
            }
        }
        setLore(newLore);
        return this;
    }

    public IBuilder replaceLorePAPI() {
        return replaceLorePAPI(null);
    }
    public IBuilder replaceLorePAPI(@Nullable OfflinePlayer player) {
        if (lore == null) { return this; }
        lore.replaceAll(s -> StringUtilP.p(player, s));
        return this;
    }

    public IBuilder replaceBoth(String find, String replacement) {
        replaceName(find, replacement);
        replaceLore(find, replacement);
        return this;
    }

    public IBuilder replaceBothPAPI() {
        return replaceBothPAPI(null);
    }
    public IBuilder replaceBothPAPI(@Nullable OfflinePlayer player) {
        replaceNamePAPI(player);
        replaceLorePAPI(player);
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

    public IBuilder disableGlow() {
        this.addGlow = false;
        return this;
    }

    public IBuilder addGlow(boolean glow) {
        this.addGlow = glow;
        return this;
    }

    public IBuilder removeEnchantment(XEnchantment enchant) {
        this.enchantments.remove(enchant);
        return this;
    }

    public IBuilder addEnchant(XEnchantment enchant, int level) {
        this.enchantments.put(enchant, level);
        return this;
    }

    public IBuilder addEnchantments(Map<XEnchantment, Integer> enchantments) {
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
    final boolean loadXMaterial(ConfigurationSection section) {
        @Nullable String strValue = null;
        if (section.isString("material")) {
            strValue = section.getString("material");
        } else if (section.isString("type")) {
            strValue = section.getString("type");
        }
        if (strValue == null) { return false; }
        strValue = strValue.toUpperCase().strip();

        // Parse this string into a Material (if available)
        @Nullable Material mat = parseMaterial(strValue);
        if (mat != null && damage == 0) {
            // As long as we don't want a data value, we're done & we can use this Material
            this.material = XMaterial.matchXMaterial(mat);
            return true;
        }

        // Parse this string into an XMaterial, this is used as a last resort
        @Nullable XMaterial defaultParse = parseXMaterial(strValue);

        // XMaterial names are latest (i.e. 1.21 or whatever) Material values, meaning if our string was "WOOL"
        // It won't return an XMaterial.WOOL (that doesn't exist)
        // Meaning materials with colors should never pass this check, and this is only for simple materials like
        // XMaterial.BUCKET which has a Material.BUCKET to match
        if (defaultParse != null && defaultParse.name().equalsIgnoreCase(strValue) && damage == 0) {
            this.material = defaultParse;
            return true;
        }

        // Now, we have a weird material name, or a material with a data value
        // We need to evaluate the correct XMaterial, which we can only do if we have an original material string
        // Why do we need to do this? Because when matching "WOOL" to an XMaterial, xseries will pick a colored wool i.e. BLACK_WOOL
        //   (due to the way they handle legacy names, all wool colors are mapped to "WOOL" and it just picks the first one it finds)
        // But if we have a data value (or even data value 0) we know that we want a specific wool color, and need a different enum value
        //   i.e. XMaterial.RED_WOOL

        // Let's try to fetch an XMaterial that corresponds to this material string & data
        // Data should have already been loaded prior to calling this
        Optional<XMaterial> o = MaterialFlatteningUtil.findMaterialAndDataMapping(strValue, (byte) damage);
        // Fall back to the original XMaterial if we can't find a specific colored match
        @Nullable XMaterial xMat = o.orElse(defaultParse);

        if (xMat != null) {
            this.material = xMat;
            return true;
        }

        return false;
    }

    /**
     * Nullable because the material string may be another IBuilder format (like ItemsAdder namespacedID)
     */
    public @Nullable XMaterial parseXMaterial(String mat) {
        try {
            return XMaterial.matchXMaterial(mat).orElse(null);
        } catch (Throwable t) {
            return null;
        }
    }

    public @Nullable Material parseMaterial(String mat) {
        try {
            return Material.getMaterial(mat);
        } catch (Throwable t) {
            return null;
        }
    }

    public abstract void loadTypes(ConfigurationSection config);

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
