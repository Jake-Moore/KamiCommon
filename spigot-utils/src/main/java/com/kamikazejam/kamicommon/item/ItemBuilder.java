package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.item.patch.Patch;
import com.kamikazejam.kamicommon.item.patch.PatchAdd;
import com.kamikazejam.kamicommon.item.patch.PatchOp;
import com.kamikazejam.kamicommon.item.patch.PatchRemove;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.StringUtilP;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A builder class for wrapping {@link ItemStack}s and applying patches to them.<br>
 * Patches modify the base metadata and nbt of the item via easier to use methods exposed by this class.<br>
 * <br>
 * <strong>NOTE:</strong> Using third party plugins like ItemsAdder which provide custom items IS SUPPORTED!<br>
 * Just adjust your workflow to parse the {@link ItemStack} from their API, and then use that object as the prototype for this wrapper:
 * <pre>
 *     ItemStack customItem = CustomStack.getInstance("my_namespaced_id");
 *     ItemBuilder builder = new ItemBuilder(customItem);
 *     // apply patches to the builder...
 *     ItemStack finalItem = builder.build();
 * </pre>
 * <br>
 * There is also support in the loaders for using external {@link ItemStack} prototypes using {@link ItemBuilderLoader#loadPatches(ItemStack, ConfigurationSection)}:
 * <pre>
 *     ItemStack customItem = CustomStack.getInstance("my_namespaced_id");
 *     ItemBuilder builder = ItemBuilderLoader.loadPatches(customItem, configSection);
 *     // apply patches to the builder...
 *     ItemStack finalItem = builder.build();
 * </pre>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class ItemBuilder implements IBuilder<ItemBuilder>, Cloneable {

    // ------------------------------------------------------------ //
    //                       PROTOTYPE STORAGE                      //
    // ------------------------------------------------------------ //
    /**
     * The initial version of the {@link ItemStack} being built.<br>
     * <br>
     * This is the raw version supplied to the constructors, BEFORE patches are applied.<br>
     * It has a fixed {@link Material} from its creation, but other properties may be mutable by patches.<br>
     * <br>
     * Use {@link #build()} to get the final version of the item with all patches applied.
     */
    private final @NotNull ItemStack prototype;

    // ------------------------------------------------------------ //
    //                       PATCH PROPERTIES                       //
    // ------------------------------------------------------------ //
    /**
     * PATCH PROPERTY (null = inherits prototype value):<br>
     * <br>
     * The quantity of items in the stack.<br>
     * <br>
     * Range: 1 to {@link ItemStack#getMaxStackSize()} (inclusive) Of the {@link #getPrototype()}
     */
    private @Nullable Integer amount = null;

    /**
     * PATCH PROPERTY (null = inherits prototype value):<br>
     * <br>
     * The damage (reduced durability) of the item.<br>
     * Set to 0 for an undamaged item.
     */
    private @Nullable Integer damage = null;

    /**
     * PATCH PROPERTY (null = inherits prototype value):<br>
     * <br>
     * A custom display name for the item.<br>
     * No color translations are applied, process them BEFORE setting the name.
     */
    private @Nullable String name = null;

    /**
     * PATCH PROPERTY (null = inherits prototype value):<br>
     * <br>
     * Custom lore for the item.<br>
     * No color translations are applied, process them BEFORE setting the lore.
     */
    private @Nullable List<String> lore = null;

    /**
     * PATCH PROPERTY (null = inherits prototype value):<br>
     * <br>
     * If the item is unbreakable or not.
     */
    private @Nullable Boolean unbreakable = null;

    /**
     * PATCH PROPERTY (empty by default, add flags states to override prototype):<br>
     * <br>
     * Item flags to apply or remove from the item.
     */
    private final @NotNull Map<XItemFlag, PatchOp> itemFlags = new HashMap<>();

    /**
     * PATCH PROPERTY (empty by default, enchantments patches that override the prototype):<br>
     * <br>
     * Enchantments to apply to the item, with their levels.<br>
     * If an enchantment for one of these patches is already present on the prototype, it will be overridden by this patch.
     */
    private final @NotNull Map<XEnchantment, Patch<Integer>> enchantments = new HashMap<>();

    /**
     * PATCH PROPERTY (false by default):<br>
     * <br>
     * If the item should have a glow effect (enchantment glint) added.<br>
     * If enabled, then the item will have a glint added, even if it has no enchantments.
     */
    private boolean addGlow = false;

    /**
     * PATCH PROPERTY (null by default, set to override prototype):<br>
     * <br>
     * Special Patch Properties that applies ONLY to valid {@link SkullMeta} items.<br>
     * If set, this will override the skull owner of the item.
     */
    private @Nullable String skullOwner = null; // player name



    // ------------------------------------------------------------ //
    //                         CONSTRUCTORS                         //
    // ------------------------------------------------------------ //
    /**
     * Construct a new ItemBuilder from a prototype {@link ItemStack}.<br>
     * (The prototype is never modified, it is immutable, and accessed via {@link #getPrototype()})<br>
     * <br>
     * Call ItemBuilder methods to set and update item 'patches', and then use {@link #build()} to construct the final item.<br>
     * <br>
     * @param prototype The initial item this class applies patches on top of.
     */
    public ItemBuilder(@NotNull ItemStack prototype) {
        Preconditions.checkNotNull(prototype, "Prototype ItemStack cannot be null");
        this.prototype = prototype;
    }

    /**
     * Construct a new ItemBuilder from an {@link XMaterial}.<br>
     * <br>
     * This is equivalent to calling {@link #ItemBuilder(ItemStack)} with the result of {@link XMaterial#parseItem()}
     */
    public ItemBuilder(@NotNull XMaterial material) {
        this(
                Preconditions.checkNotNull(
                        Preconditions.checkNotNull(material, "XMaterial cannot be null").parseItem(),
                        "XMaterial " + material.name() + " could not be parsed to a valid ItemStack!"
                )
        );
    }

    /**
     * Construct a new ItemBuilder from an {@link Material}.<br>
     * <br>
     * This is equivalent to calling {@link #ItemBuilder(XMaterial)} with the result of {@link XMaterial#matchXMaterial(Material)}
     */
    public ItemBuilder(@NotNull Material material) {
        this(XMaterial.matchXMaterial(
                Preconditions.checkNotNull(material, "Material cannot be null")
        ));
    }

    // ------------------------------------------------------------ //
    //                           PROTOTYPE                          //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull ItemStack getPrototype() {
        return this.prototype;
    }

    // ------------------------------------------------------------ //
    //                             BUILD                            //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull ItemStack build(@Nullable Player viewer) {
        @NotNull ItemStack stack = this.prototype.clone();
        // Amount
        if (amount != null) {
            stack.setAmount(amount);
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) { return stack; }

        // Name and lore
        if (name != null) {
            meta.setDisplayName(StringUtilP.p(viewer, name));
        }
        if (lore != null) {
            meta.setLore(StringUtilP.p(viewer, lore));
        }

        // Unbreakable
        if (unbreakable != null) {
            meta = NmsAPI.getItemEditor().setUnbreakable(meta, unbreakable);
        }

        // Item Flags
        if (!itemFlags.isEmpty()) {
            for (Entry<XItemFlag, PatchOp> entry : itemFlags.entrySet()) {
                @Nullable ItemFlag flag = entry.getKey().get();
                if (flag == null) {
                    continue;
                }
                switch (entry.getValue()) {
                    case ADD -> meta.addItemFlags(flag);
                    case REMOVE -> meta.removeItemFlags(flag);
                }
            }
        }

        // Enchantments
        if (!enchantments.isEmpty()) {
            for (Map.Entry<XEnchantment, Patch<Integer>> entry : enchantments.entrySet()) {
                Enchantment enchant = Preconditions.checkNotNull(
                        entry.getKey().get(),
                        "XEnchantment '" + entry.getKey().name() + "' failed to correspond to a valid Bukkit enchantment!"
                );
                switch (entry.getValue()) {
                    case PatchRemove<Integer> remove -> meta.removeEnchant(enchant);
                    case PatchAdd<Integer> add -> {
                        if (add.getValue() <= 0) {
                            // skip invalid levels
                            continue;
                        }
                        meta.addEnchant(enchant, add.getValue(), true); // true - ignores level restrictions
                    }
                }
            }
        }

        // Glow (only if no enchants are present, therefore need to add a fake one)
        if (addGlow && meta.getEnchants().isEmpty()) {
            ItemFlag flag = Preconditions.checkNotNull(
                    XItemFlag.HIDE_ENCHANTS.get(),
                    "XItemFlag.HIDE_ENCHANTS could not be resolved to a valid ItemFlag!"
            );
            meta.addEnchant(XEnchantment.INFINITY.get(), 1, true);
            if (!meta.getItemFlags().contains(flag)) {
                meta.addItemFlags(flag);
            }
        }

        // Skull Meta
        if (skullOwner != null && meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwner(skullOwner);
        }

        // Apply meta
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public @NotNull ItemBuilder clone() {
        try {
            // 1) Shallow copy of this object (fields copied as-is)
            ItemBuilder copy = (ItemBuilder) super.clone();

            // 2) Fix up mutable fields to avoid shared state

            // amount, damage, name, unbreakable, skullOwner are immutable/boxed; no action needed

            // lore: create a new list if present
            if (this.lore != null) {
                copy.lore = new ArrayList<>(this.lore);
            } else {
                copy.lore = null;
            }

            // itemFlags: copy entries into a new map
            if (!this.itemFlags.isEmpty()) {
                copy.itemFlags.clear(); // ensure target map is empty first
                copy.itemFlags.putAll(this.itemFlags);
            } else {
                copy.itemFlags.clear();
            }

            // enchantments: copy entries into a new map
            if (!this.enchantments.isEmpty()) {
                copy.enchantments.clear();
                copy.enchantments.putAll(this.enchantments);
            } else {
                copy.enchantments.clear();
            }

            // addGlow is primitive and already copied by super.clone()

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    // ------------------------------------------------------------ //
    //                  IBuilder Patch Management                   //
    // ------------------------------------------------------------ //
    // ----- AMOUNT ------ //
    @Override
    public @NotNull ItemBuilder setAmount(int amount) {
        this.amount = Math.max(1, Math.min(amount, prototype.getMaxStackSize()));
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetAmount() {
        this.amount = null;
        return this;
    }

    // ----- DAMAGE (FOR DAMAGEABLE ITEMS) ----- //
    @Override
    public @NotNull ItemBuilder setDamage(int damage) {
        if (!willUseDamage()) {
            // do nothing, item does not support damage
            return this;
        }
        this.damage = Math.max(0, damage);
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetDamage() {
        if (!willUseDamage()) {
            // do nothing, item does not support damage
            return this;
        }
        this.damage = null;
        return this;
    }

    // ----- NAME ------ //
    @Override
    public @NotNull ItemBuilder setName(@NotNull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        this.name = name;
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetName() {
        this.name = null;
        return this;
    }

    // ----- LORE ------ //
    @Override
    public @NotNull ItemBuilder setLore(@NotNull String... loreLines) {
        Preconditions.checkNotNull(loreLines, "Lore lines cannot be null");
        return setLore(Arrays.asList(loreLines));
    }

    @Override
    public @NotNull ItemBuilder setLore(@NotNull List<String> loreLines) {
        Preconditions.checkNotNull(loreLines, "Lore lines cannot be null");
        this.lore = loreLines;
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetLore() {
        this.lore = null;
        return this;
    }

    // ----- UNBREAKABLE ------ //
    @Override
    public @NotNull ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetUnbreakable() {
        this.unbreakable = null;
        return this;
    }

    // ----- ITEM FLAGS ------ //
    @Override
    public @NotNull ItemBuilder addItemFlag(@NotNull XItemFlag flag) {
        Preconditions.checkNotNull(flag, "ItemFlag cannot be null");
        this.itemFlags.put(flag, PatchOp.ADD);
        return this;
    }

    @Override
    public @NotNull ItemBuilder addItemFlags(@NotNull Collection<XItemFlag> flags) {
        Preconditions.checkNotNull(flags, "ItemFlags collection cannot be null");
        for (XItemFlag flag : flags) {
            addItemFlag(flag);
        }
        return this;
    }

    @Override
    public @NotNull ItemBuilder removeItemFlag(@NotNull XItemFlag flag) {
        Preconditions.checkNotNull(flag, "ItemFlag cannot be null");
        this.itemFlags.put(flag, PatchOp.REMOVE);
        return this;
    }

    @Override
    public @NotNull ItemBuilder removeItemFlags(@NotNull Collection<XItemFlag> flags) {
        Preconditions.checkNotNull(flags, "ItemFlags collection cannot be null");
        for (XItemFlag flag : flags) {
            removeItemFlag(flag);
        }
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetItemFlag(@NotNull XItemFlag flag) {
        Preconditions.checkNotNull(flag, "ItemFlag cannot be null");
        this.itemFlags.remove(flag);
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetAllItemFlags() {
        this.itemFlags.clear();
        return this;
    }

    // ----- ENCHANTMENTS ------ //
    @Override
    public @NotNull ItemBuilder setEnchantment(@NotNull XEnchantment enchant, int level) {
        Preconditions.checkNotNull(enchant, "XEnchantment cannot be null");
        this.enchantments.put(enchant, new PatchAdd<>(level));
        return this;
    }

    @Override
    public @NotNull ItemBuilder setEnchantments(@NotNull Map<XEnchantment, Integer> enchantments) {
        Preconditions.checkNotNull(enchantments, "Enchantments map cannot be null");
        for (Entry<XEnchantment, Integer> entry : enchantments.entrySet()) {
            setEnchantment(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public @NotNull ItemBuilder removeEnchantment(@NotNull XEnchantment enchant) {
        Preconditions.checkNotNull(enchant, "XEnchantment cannot be null");
        this.enchantments.put(enchant, new PatchRemove<>());
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetEnchantment(@NotNull XEnchantment enchant) {
        Preconditions.checkNotNull(enchant, "XEnchantment cannot be null");
        this.enchantments.remove(enchant);
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetAllEnchantments() {
        this.enchantments.clear();
        return this;
    }

    // ----- GLOW ----- //
    @Override
    public @NotNull ItemBuilder addGlow() {
        this.addGlow = true;
        return this;
    }

    @Override
    public @NotNull ItemBuilder removeGlow() {
        this.addGlow = false;
        return this;
    }

    // ----- SKULL OWNER (FOR PLAYER HEADS) ----- //
    @Override
    public @NotNull ItemBuilder setSkullOwner(@NotNull String skullOwner) {
        Preconditions.checkNotNull(skullOwner, "Skull owner cannot be null");
        if (!willUseSkullOwner()) {
            // do nothing, item does not support skull owners
            return this;
        }
        this.skullOwner = skullOwner;
        return this;
    }

    @Override
    public @NotNull ItemBuilder resetSkullOwner() {
        if (!willUseSkullOwner()) {
            // do nothing, item does not support skull owners
            return this;
        }
        this.skullOwner = null;
        return this;
    }



    // ------------------------------------------------------------ //
    //                  IBuilder Property Getters                   //
    // ------------------------------------------------------------ //
    @Override
    public int getAmount() {
        return (amount != null) ? amount : prototype.getAmount();
    }

    @Override
    public @Nullable String getName() {
        if (name != null) { return name; }
        @Nullable ItemMeta meta = prototype.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) { return null; }
        return meta.getDisplayName();
    }

    @Override
    public @Nullable List<@NotNull String> getLore() {
        if (lore != null) { return lore; }
        @Nullable ItemMeta meta = prototype.getItemMeta();
        if (meta == null || !meta.hasLore()) { return null; }
        return meta.getLore();
    }

    @Override
    public boolean isUnbreakable() {
        if (unbreakable != null) { return unbreakable; }
        @Nullable ItemMeta meta = prototype.getItemMeta();
        if (meta == null) { return false; }
        return NmsAPI.getItemEditor().isUnbreakable(meta);
    }

    @Override
    public boolean hasItemFlag(@NotNull XItemFlag flag) {
        @Nullable PatchOp op = itemFlags.get(flag);
        if (op != null) {
            switch (op) {
                case ADD -> { return true; }
                case REMOVE -> { return false; }
            }
        }

        @Nullable ItemMeta meta = prototype.getItemMeta();
        if (meta == null) { return false; }
        @Nullable ItemFlag flagBukkit = flag.get();
        if (flagBukkit == null) { return false; }
        return meta.hasItemFlag(flagBukkit);
    }

    @Override
    public int getEnchantmentLevel(@NotNull XEnchantment enchant) {
        @Nullable Patch<Integer> patch = enchantments.get(enchant);
        if (patch != null) {
            switch (patch) {
                case PatchAdd<Integer> add -> { return add.getValue(); }
                case PatchRemove<Integer> remove -> { return 0; }
            }
        }

        @NotNull Enchantment enchantment = Preconditions.checkNotNull(
                enchant.get(),
                "XEnchantment '" + enchant.name() + "' failed to correspond to a valid Bukkit enchantment!"
        );
        @Nullable ItemMeta meta = prototype.getItemMeta();
        if (meta == null || !meta.hasEnchant(enchantment)) { return 0; }
        return meta.getEnchantLevel(enchantment);
    }

    @Override
    public boolean hasGlow() {
        return addGlow;
    }

    @Override
    public @Nullable String getSkullOwner() {
        if (!willUseSkullOwner()) {
            return null;
        }
        if (skullOwner != null) { return skullOwner; }
        @Nullable ItemMeta meta = prototype.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) { return null; }
        return skullMeta.getOwner();
    }

    @Override
    public boolean willUseSkullOwner() {
        @Nullable ItemMeta meta = prototype.getItemMeta();
        return meta instanceof SkullMeta;
    }

    @Override
    public int getDamage() {
        if (!willUseDamage()) {
            return 0;
        }
        if (damage != null) { return damage; }
        return NmsAPI.getItemEditor().getDamage(prototype);
    }

    @Override
    public boolean willUseDamage() {
        return NmsAPI.getItemEditor().isDamageable(prototype);
    }



    // ------------------------------------------------------------ //
    //                    PATCH PROPERTY HELPERS                    //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull ItemBuilder replaceName(@NotNull String find, @NotNull String replacement) {
        if (name == null) { return this; }
        name = name.replace(find, replacement);
        return this;
    }

    @Override
    public @NotNull ItemBuilder replaceNamePAPI(@Nullable OfflinePlayer player) {
        if (name == null) { return this; }
        name = StringUtilP.p(player, name);
        return this;
    }

    @Override
    @NotNull
    public ItemBuilder replaceLoreLine(@NotNull String find, @NotNull List<@NotNull String> replacement) {
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

    @Override
    @NotNull
    public ItemBuilder replaceLore(@NotNull String find, @NotNull String replacement) {
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

    @Override
    @NotNull
    public ItemBuilder replaceLorePAPI(@Nullable OfflinePlayer player) {
        if (lore == null) { return this; }
        lore.replaceAll(s -> StringUtilP.p(player, s));
        return this;
    }

    @Override
    @NotNull
    public ItemBuilder addLoreLines(@NotNull List<@NotNull String> lines) {
        if (lore == null) { lore = new ArrayList<>(); }
        lore.addAll(lines);
        return this;
    }



    // ------------------------------------------------------------ //
    //                        Static Loaders                        //
    // ------------------------------------------------------------ //
    /**
     * Load a full {@link ItemBuilder} from a configuration section.<br>
     * <br>
     * The section can define the material, amount, name, lore, and many additional attributes of the item.
     *
     * @return The loaded {@link ItemBuilder} where the config values were set as IBuilder PATCHES.
     * @throws IllegalArgumentException If any part of the parsing failed (for example if no valid material was defined).
     */
    public static @NotNull ItemBuilder load(@NotNull ConfigurationSection section) {
        Preconditions.checkNotNull(section, "ConfigurationSection cannot be null");
        return ItemBuilderLoader.load(section);
    }
}
