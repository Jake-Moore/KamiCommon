package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.configuration.loader.ItemTypeLoader;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Loader to assist in automatically extracting {@link ItemBuilder}s from configuration sections.<br>
 * <br>
 * Supports the {@link KamiConfig} system, requiring a {@link ConfigurationSection} object to load from.
 */
public class ItemBuilderLoader {
    /**
     * Load a full {@link ItemBuilder} from a configuration section.<br>
     * <br>
     * The section can define the material, amount, name, lore, and many additional attributes of the item.
     *
     * @return The loaded {@link ItemBuilder} where the config values were set as IBuilder PATCHES.
     * @throws IllegalArgumentException If any part of the parsing failed (for example if no valid material was defined).
     */
    @NotNull
    public static ItemBuilder load(@NotNull ConfigurationSection section) {
        @Nullable XMaterial type = ItemTypeLoader.loadType(section);
        @Nullable ItemStack prototype = (type != null) ? type.parseItem() : null;
        if (type == null || prototype == null) {
            throw new IllegalArgumentException("Could not load ItemBuilder: No valid material defined in the configuration section");
        }
        return loadPatches(prototype, section);
    }

    /**
     * Load an {@link ItemBuilder} from a prototype {@link ItemStack} and a configuration section which defines additional patches to apply to the item.<br>
     * <br>
     * The section can define the amount, name, lore, and many additional attributes of the item. (Everything except the material type)
     * @param prototype The prototype item stack to base the ItemBuilder on, this defines the material type and any existing item meta.
     * @param section The configuration section to load additional patches from.
     */
    @NotNull
    public static ItemBuilder loadPatches(@NotNull ItemStack prototype, @NotNull ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(prototype);
        // Load properties from config
        @Nullable Integer amount = (section.isSet("amount") && section.isInt("amount")) ? section.getInt("amount") : null;
        @Nullable Integer damage = (section.isSet("damage") && section.isInt("damage")) ? section.getInt("damage") : null;
        @Nullable String name = section.getString("name", null);
        @Nullable List<String> lore = (section.isSet("lore") && section.isList("lore")) ? section.getStringList("lore", null) : null;
        @Nullable Boolean unbreakable = (section.isSet("unbreakable") && section.isBoolean("unbreakable")) ? section.getBoolean("unbreakable") : null;
        @Nullable Set<XItemFlag> itemFlags = loadItemFlags(section);
        @Nullable Map<XEnchantment, Integer> enchantments = loadEnchantments(section);
        boolean glow = section.getBoolean("glow", false) || section.getBoolean("addGlow", false);
        @Nullable String skullOwner = section.getString("skull-owner", null);

        // Apply non-null properties as patches to ItemBuilder
        if (amount != null) { builder.setAmount(amount); }
        if (damage != null) { builder.setDamage(damage); }
        if (name != null) { builder.setName(name); }
        if (lore != null) { builder.setLore(lore); }
        if (unbreakable != null) { builder.setUnbreakable(unbreakable); }
        if (itemFlags != null) { builder.addItemFlags(itemFlags); }
        if (enchantments != null) { builder.setEnchantments(enchantments); }
        if (glow) { builder.addGlow(); }
        if (skullOwner != null && !skullOwner.isEmpty()) { builder.setSkullOwner(skullOwner); }

        return builder;
    }

    /**
     * Loads a map of enchantments from a configuration section.<br>
     * <br>
     * The section should contain a sub-section "enchantments" where each key is the name of the enchantment and the value is the level (integer).<br>
     * <br>
     * NOTE: Invalid enchantment names or levels (<= 0) will not be parsed and will be ignored.
     *
     * @return A non-null map of enchantments to levels, or {@code null} if no enchantments were defined or valid.
     */
    private static @Nullable Map<XEnchantment, Integer> loadEnchantments(@NotNull ConfigurationSection section) {
        if (!section.isConfigurationSection("enchantments")) { return null; }
        Map<XEnchantment, Integer> enchantments = new HashMap<>();

        // Load the sub-keys
        for (String key : section.getConfigurationSection("enchantments").getKeys(false)) {
            XEnchantment enchant = XEnchantment.of(key).orElse(null);
            if (enchant == null) {
                continue;
            }
            int level = section.getInt("enchantments." + key);
            if (level <= 0) {
                continue;
            }
            enchantments.put(enchant, level);
        }

        return enchantments.isEmpty() ? null : enchantments;
    }

    /**
     * Loads a set of item flags from a configuration section.<br>
     * <br>
     * The section should contain a list of strings under the key {@code item-flags}, where each string is the name of an item flag.<br>
     * <br>
     * NOTE: Invalid item flag names will not be parsed and will be ignored.
     *
     * @return A non-null list of item flags, or {@code null} if no item flags were defined or valid.
     */
    private static @Nullable Set<XItemFlag> loadItemFlags(@NotNull ConfigurationSection section) {
        if (!section.isSet("item-flags") || !section.isList("item-flags")) { return null; }
        Set<XItemFlag> itemFlags = new HashSet<>();

        for (String flagName : section.getStringList("item-flags")) {
            try {
                // Parse by enum first
                XItemFlag flag = XItemFlag.valueOf(flagName.toUpperCase().replace(" ", "_"));
                itemFlags.add(flag);
            } catch (IllegalArgumentException | NoSuchElementException e) {
                // Try their custom method
                XItemFlag.of(flagName).ifPresent(itemFlags::add);
            }
        }

        return itemFlags.isEmpty() ? null : itemFlags;
    }
}
