package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.SoftPlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue", "BooleanMethodIsAlwaysInverted"})
public sealed interface IBuilder<T extends IBuilder<T>> extends Cloneable permits ItemBuilder {

    // ------------------------------------------------------------ //
    //                           PROTOTYPE                          //
    // ------------------------------------------------------------ //
    /**
     * Get the prototype {@link ItemStack} this builder is based on.<br>
     * <br>
     * The prototype is never modified, it is immutable, and accessed via this method.<br>
     * Use {@link #build()} to get the final version of the item with all patches applied.
     *
     * @return The prototype {@link ItemStack} this builder is based on.
     */
    @NotNull ItemStack getPrototype();

    /**
     * Get the {@link XMaterial} of the prototype item this builder is based on.<br>
     * <br>
     * This is a convenience method, equivalent to calling {@link XMaterial#matchXMaterial(ItemStack)} on the prototype.
     *
     * @return The {@link XMaterial} of the prototype item this builder is based on.
     */
    default @NotNull XMaterial getMaterial() {
        return XMaterial.matchXMaterial(getPrototype());
    }



    // ------------------------------------------------------------ //
    //                   PATCH PROPERTY MANAGEMENT                  //
    // ------------------------------------------------------------ //
    // ----- AMOUNT ------ //
    /**
     * PATCH FUNCTION - Sets the amount of items in the stack.<br>
     * Range: 1 to {@link ItemStack#getMaxStackSize()} (inclusive) of the prototype.<br>
     * <br>
     * Clear this patch by calling {@link #resetAmount()}.
     * <br>
     * (If the amount is out of range, it will be clamped within the valid range.)
     * @return This builder, for chaining
     */
    @NotNull
    T setAmount(int amount);

    /**
     * PATCH FUNCTION - Clears the amount patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: The prototype's amount is now used by the builder, this only removes any existing amount patch.
     * @return This builder, for chaining
     */
    @NotNull
    T resetAmount();

    // ----- DAMAGE (FOR DAMAGEABLE ITEMS) ----- //
    /**
     * PATCH FUNCTION - Sets the damage of a damageable item.<br>
     * <br>
     * NOTE: This function has no effect on the item unless {@link #willUseDamage()} returns true,
     * otherwise the item will be unaffected and {@link #getDamage()} will always return 0.<br>
     * <br>
     * Clear this patch by calling {@link #resetDamage()}.
     * <br>
     * @return This builder, for chaining
     * @param damage The damage value to set (0 = undamaged)
     */
    @NotNull
    T setDamage(int damage);

    /**
     * Alias of {@link #setDamage(int)}.
     * @deprecated As of 5.0.0-alpha.17, replaced by {@link #setDamage(int)}.
     */
    @Deprecated(since = "5.0.0-alpha.17")
    @NotNull
    default T setDurability(short dur) {
        return setDamage(dur);
    }

    /**
     * Alias of {@link #setDamage(int)}.
     * @deprecated As of 5.0.0-alpha.17, replaced by {@link #setDamage(int)}.
     */
    @Deprecated(since = "5.0.0-alpha.17")
    @NotNull
    default T setDurability(int dur) {
        return setDamage(dur);
    }

    /**
     * PATCH FUNCTION - Clears the damage patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: This function has no effect on the item unless {@link #willUseDamage()} returns true,
     * otherwise the item will be unaffected and {@link #getDamage()} will always return 0.<br>
     * <br>
     * NOTE: The prototype's damage is now used by the builder (if applicable), this only removes any existing damage patch.
     * @return This builder, for chaining
     */
    @NotNull
    T resetDamage();

    // ----- NAME ------ //
    /**
     * Alias of {@link #setDisplayName(String)}.
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #displayName(VersionedComponent)}.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T setName(@NotNull String name) {
        return setDisplayName(name);
    }

    /**
     * PATCH FUNCTION - Applies a custom display name for the item.<br>
     * Colors are translated automatically via {@link LegacyColors#t(String)}.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * Clear this patch by calling {@link #resetName()}.
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #displayName(VersionedComponent)}.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T setDisplayName(@NotNull String name) {
        Preconditions.checkNotNull(name, "Display name cannot be null");
        // Convert to component and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        return this.displayName(serializer.fromLegacySection(LegacyColors.t(name)));
    }

    /**
     * PATCH FUNCTION - Applies a custom display name for the item.<br>
     * Colors are assumed to be already handled in the component.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * Clear this patch by calling {@link #resetName()}.
     * @return This builder, for chaining
     */
    @NotNull
    T displayName(@NotNull VersionedComponent name);

    /**
     * PATCH FUNCTION - Clears the name patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: The prototype's name is now used by the builder, this only removes any existing name patch.
     * @return This builder, for chaining
     */
    @NotNull
    T resetName();

    // ----- LORE ------ //
    /**
     * PATCH FUNCTION - Sets the custom lore for the item.<br>
     * Colors are translated automatically via {@link LegacyColors#t(String)}.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * Clear this patch by calling {@link #resetLore()}.
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #lore(VersionedComponent...)}.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T setLore(@NotNull String... line) {
        Preconditions.checkNotNull(line, "Lore lines cannot be null");
        // Convert to component and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        VersionedComponent[] components = new VersionedComponent[line.length];
        for (int i = 0; i < line.length; i++) {
            components[i] = serializer.fromLegacySection(LegacyColors.t(line[i]));
        }
        return this.lore(components);
    }

    /**
     * PATCH FUNCTION - Sets the custom lore for the item.<br>
     * Colors are translated automatically via {@link LegacyColors#t(String)}.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * Clear this patch by calling {@link #resetLore()}.
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #lore(List)}
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T setLore(@NotNull List<@NotNull String> lore) {
        Preconditions.checkNotNull(lore, "Lore lines cannot be null");
        // Convert to components and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        List<VersionedComponent> components = new ArrayList<>(lore.size());
        for (String line : lore) {
            components.add(serializer.fromLegacySection(LegacyColors.t(line)));
        }
        return this.lore(components);
    }

    /**
     * PATCH FUNCTION - Sets the custom lore for the item.<br>
     * Colors are assumed to be already handled in the components.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * Clear this patch by calling {@link #resetLore()}.
     * @return This builder, for chaining
     */
    @NotNull
    default T lore(@NotNull VersionedComponent... lines) {
        return lore(Arrays.asList(lines));
    }

    /**
     * PATCH FUNCTION - Sets the custom lore for the item.<br>
     * Colors are assumed to be already handled in the components.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * Clear this patch by calling {@link #resetLore()}.
     * @return This builder, for chaining
     */
    @NotNull
    T lore(@NotNull List<@NotNull VersionedComponent> lines);

    /**
     * PATCH FUNCTION - Clears the lore patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: The prototype's lore is now used by the builder, this only removes any existing lore patch.
     * @return This builder, for chaining
     */
    @NotNull
    T resetLore();

    /**
     * PATCH FUNCTION - Sets the lore to an empty list, effectively removing all lore from the item.<br>
     * This is equivalent to calling {@link #setLore(List)} with an empty list.<br>
     * <br>
     * This differs from {@link #resetLore()} which clears the patch and uses the prototype's lore.
     * @return This builder, for chaining
     */
    @NotNull
    default T removeLore() {
        return lore(new ArrayList<>());
    }

    /**
     * PATCH FUNCTION - Appends additional lines to the current lore.<br>
     * If no lore patch is currently set, this will create a new lore patch with the provided lines.<br>
     * If a lore patch is already set, the new lines will be added to the end of the existing lore.<br>
     * <br>
     * Colors are translated automatically via {@link LegacyColors#t(String)}.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * @param lines The lore lines to append
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #addLoreComponents(List)}
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T addLoreLines(@NotNull List<@NotNull String> lines) {
        Preconditions.checkNotNull(lines, "Lore lines cannot be null");
        // Convert to components and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        List<VersionedComponent> components = new ArrayList<>(lines.size());
        for (String line : lines) {
            components.add(serializer.fromLegacySection(LegacyColors.t(line)));
        }
        return this.addLoreComponents(components);
    }

    /**
     * PATCH FUNCTION - Appends additional lines to the current lore.<br>
     * If no lore patch is currently set, this will create a new lore patch with the provided lines.<br>
     * If a lore patch is already set, the new lines will be added to the end of the existing lore.<br>
     * <br>
     * Colors are translated automatically via {@link LegacyColors#t(String)}.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * @param lines The lore lines to append
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #addLoreComponents(VersionedComponent...)}
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T addLoreLines(@NotNull String... lines) {
        return addLoreLines(Arrays.asList(lines));
    }

    /**
     * PATCH FUNCTION - Appends additional lines to the current lore.<br>
     * If no lore patch is currently set, this will create a new lore patch with the provided lines.<br>
     * If a lore patch is already set, the new lines will be added to the end of the existing lore.<br>
     * <br>
     * Colors are assumed to be already handled in the components.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * @param lines The lore lines to append
     * @return This builder, for chaining
     */
    @NotNull
    T addLoreComponents(@NotNull List<@NotNull VersionedComponent> lines);

    /**
     * PATCH FUNCTION - Appends additional lines to the current lore.<br>
     * If no lore patch is currently set, this will create a new lore patch with the provided lines.<br>
     * If a lore patch is already set, the new lines will be added to the end of the existing lore.<br>
     * <br>
     * Colors are assumed to be already handled in the components.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * @param lines The lore lines to append
     * @return This builder, for chaining
     */
    @NotNull
    default T addLoreComponents(@NotNull VersionedComponent... lines) {
        return addLoreComponents(Arrays.asList(lines));
    }

    // ----- UNBREAKABLE ------ //
    /**
     * PATCH FUNCTION - Sets if the item is unbreakable or not.<br>
     * <br>
     * Clear this patch by calling {@link #resetUnbreakable()}.
     * @return This builder, for chaining
     */
    @NotNull
    T setUnbreakable(boolean unbreakable);

    /**
     * PATCH FUNCTION - Clears the unbreakable patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: The prototype's unbreakable value is now used by the builder, this only removes any existing unbreakable patch.
     * @return This builder, for chaining
     */
    @NotNull
    T resetUnbreakable();

    // ----- ITEM FLAGS ------ //
    /**
     * PATCH FUNCTION - Adds an item flag to the item.<br>
     * <br>
     * Remove an item flag via {@link #removeItemFlag(XItemFlag)}.
     * @return This builder, for chaining
     */
    @NotNull
    T addItemFlag(@NotNull XItemFlag flag);

    /**
     * PATCH FUNCTION - Adds several item flags to the item.<br>
     * <br>
     * Remove an item flag via {@link #removeItemFlag(XItemFlag)}.
     * @return This builder, for chaining
     */
    @NotNull
    T addItemFlags(@NotNull Collection<XItemFlag> flags);

    /**
     * PATCH FUNCTION - Removes an item flag from the item.<br>
     * <br>
     * Add an item flag via {@link #addItemFlag(XItemFlag)}.
     * @return This builder, for chaining
     */
    @NotNull
    T removeItemFlag(@NotNull XItemFlag flag);

    /**
     * PATCH FUNCTION - Removes several item flags from the item.<br>
     * <br>
     * Add an item flag via {@link #addItemFlag(XItemFlag)}.
     * @return This builder, for chaining
     */
    @NotNull
    T removeItemFlags(@NotNull Collection<XItemFlag> flags);

    /**
     * PATCH FUNCTION - Sets the presence of an item flag on the item.<br>
     * <br>
     * @param present If true, the flag will be added, if false it will be removed.
     * @return This builder, for chaining
     */
    @NotNull
    default T setItemFlag(@NotNull XItemFlag flag, boolean present) {
        if (present) {
            return addItemFlag(flag);
        } else {
            return removeItemFlag(flag);
        }
    }

    /**
     * PATCH FUNCTION - Resets an item flag patch, the Builder will then use the prototype's value for this flag.<br>
     * <br>
     * Other Methods:<br>
     * - ADD an item flag via {@link #addItemFlag(XItemFlag)}<br>
     * - REMOVE an item flag via {@link #removeItemFlag(XItemFlag)}<br>
     * @return This builder, for chaining
     */
    @NotNull
    T resetItemFlag(@NotNull XItemFlag flag);

    /**
     * PATCH FUNCTION - Clears all item flag patches, the Builder will then use the prototype's values.<br>
     * <br>
     * NOTE: The prototype's item flags will still be applied, this only clears the patches.
     */
    @NotNull
    T resetAllItemFlags();

    /**
     * PATCH FUNCTION HELPER - Convenience method to hide common item attributes by adding multiple item flags.<br>
     * This method adds the following {@link XItemFlag}s to the item:<br>
     * - {@link XItemFlag#HIDE_ATTRIBUTES}<br>
     * - {@link XItemFlag#HIDE_ENCHANTS}<br>
     * - {@link XItemFlag#HIDE_PLACED_ON}<br>
     * - {@link XItemFlag#HIDE_UNBREAKABLE}<br>
     * - {@link XItemFlag#HIDE_ADDITIONAL_TOOLTIP}<br>
     * <br>
     * This is equivalent to calling {@link #addItemFlag(XItemFlag)} for each of the above flags.
     * @return This builder, for chaining
     */
    @NotNull
    default T hideAttributes() {
        return addItemFlag(XItemFlag.HIDE_ATTRIBUTES)
                .addItemFlag(XItemFlag.HIDE_ENCHANTS)
                .addItemFlag(XItemFlag.HIDE_PLACED_ON)
                .addItemFlag(XItemFlag.HIDE_UNBREAKABLE)
                .addItemFlag(XItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    // ----- ENCHANTMENTS ------ //
    /**
     * PATCH FUNCTION - Sets the level of an enchantment on the item.<br>
     * <br>
     * Other Methods:<br>
     * - REMOVE an enchantment via {@link #removeEnchantment(XEnchantment)}<br>
     * - RESET an enchantment (remove patch) via {@link #resetEnchantment(XEnchantment)}<br>
     * @param level The level of the enchantment to set (MUST be greater than 0)
     * @return This builder, for chaining
     */
    @NotNull
    T setEnchantment(@NotNull XEnchantment enchant, int level);

    /**
     * PATCH FUNCTION - Sets the level of several enchantments on the item.<br>
     * <br>
     * Other Methods:<br>
     * - REMOVE an enchantment via {@link #removeEnchantment(XEnchantment)}<br>
     * - RESET an enchantment (remove patch) via {@link #resetEnchantment(XEnchantment)}<br>
     * @param enchantments The map of enchantments to their levels to set (all levels MUST be greater than 0)
     * @return This builder, for chaining
     */
    @NotNull
    T setEnchantments(@NotNull Map<XEnchantment, Integer> enchantments);

    /**
     * PATCH FUNCTION - Remove an enchantment from the item.<br>
     * <br>
     * Other Methods:<br>
     * - ADD or UPDATE an enchantment via {@link #setEnchantment(XEnchantment, int)}<br>
     * - RESET an enchantment (remove patch) via {@link #resetEnchantment(XEnchantment)}<br>
     * @return This builder, for chaining
     */
    @NotNull
    T removeEnchantment(@NotNull XEnchantment enchant);

    /**
     * PATCH FUNCTION - Reset an enchantment patch, the Builder will then use the prototype's value for this enchantment.<br>
     * <br>
     * Other Methods:<br>
     * - ADD or UPDATE an enchantment via {@link #setEnchantment(XEnchantment, int)}<br>
     * - REMOVE an enchantment via {@link #removeEnchantment(XEnchantment)}<br>
     * @return This builder, for chaining
     */
    @NotNull
    T resetEnchantment(@NotNull XEnchantment enchant);

    /**
     * PATCH FUNCTION - Clears all enchantment patches, the Builder will then use the prototype's values.<br>
     * <br>
     * NOTE: The prototype's enchantments will still be applied, this only clears the patches.
     * @return This builder, for chaining
     */
    @NotNull
    T resetAllEnchantments();

    // ----- GLOW ----- //
    /**
     * PATCH FUNCTION - Sets whether the item should have an added glow effect.<br>
     * <br>
     * Clear this patch by calling {@link #removeGlow()}.
     * @return This builder, for chaining
     */
    @NotNull
    T addGlow();

    /**
     * PATCH FUNCTION - Removes the glow effect patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: The prototype's glow value is now used by the builder, this only removes any existing glow patch.<br>
     * NOTE: Even when the glow patch is removed, the item may still glow if the prototype has enchantments or a glow effect.
     * @return This builder, for chaining
     */
    @NotNull
    T removeGlow();

    /**
     * Alias of {@link #removeGlow()}.
     */
    @NotNull
    default T disableGlow() {
        return removeGlow();
    }

    /**
     * PATCH FUNCTION - Toggle the glow effect patch on or off.<br>
     * <br>
     * @param glow If true, the glow effect patch will be added, if false it will be removed.
     * @return This builder, for chaining
     */
    @NotNull
    default T setGlow(boolean glow) {
        if (glow) {
            return addGlow();
        } else {
            return removeGlow();
        }
    }

    // ----- SKULL OWNER (FOR PLAYER HEADS) ----- //
    /**
     * PATCH FUNCTION - Sets the owner of a player head item.<br>
     * <br>
     * NOTE: This function has no effect on the item unless {@link #willUseSkullOwner()} returns true,
     * otherwise the item will be unaffected and {@link #getSkullOwner()} will always return null.<br>
     * <br>
     * Clear this patch by calling {@link #resetSkullOwner()}.
     * @return This builder, for chaining
     */
    @NotNull
    T setSkullOwner(@NotNull String owner);

    /**
     * PATCH FUNCTION - Clears the skull owner patch, the Builder will then use the prototype's value.<br>
     * <br>
     * NOTE: This function has no effect on the item unless {@link #willUseSkullOwner()} returns true,
     * otherwise the item will be unaffected and {@link #getSkullOwner()} will always return null.<br>
     * <br>
     * NOTE: The prototype's skull owner is now used by the builder (if applicable), this only removes any existing skull owner patch.<br>
     * @return This builder, for chaining
     */
    @NotNull
    T resetSkullOwner();



    // ------------------------------------------------------------ //
    //                     ITEM PROPERTY GETTERS                    //
    // ------------------------------------------------------------ //
    /**
     * Get the amount of items in the stack.<br>
     * Range: 1 to {@link ItemStack#getMaxStackSize()} (inclusive) of the prototype.<br>
     * <br>
     * If the patch amount is null (not set), the prototype's amount is returned.
     */
    int getAmount();

    /**
     * Get the damage of a damageable item, or 0 if not damageable / undamaged.<br>
     * <br>
     * NOTE: This will always return 0 if {@link #willUseDamage()} returns false.
     */
    int getDamage();

    /**
     * Check if the prototype item supports setting damage (is damageable).<br>
     * <br>
     * If this returns true, then the following methods will modify the item:<br>
     * - {@link #setDamage(int)}<br>
     * - {@link #resetDamage()}<br>
     * - {@link #getDamage()}<br>
     * If this returns false, calling those methods have no effect and {@link #getDamage()} will always return 0.
     */
    boolean willUseDamage();

    /**
     * Get the custom display name for the item.<br>
     * <br>
     * If the patch name is null (not set), the prototype's name will be returned (if available).
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #displayName()} which returns a component.
     */
    @Deprecated
    @Nullable
    default String getName() {
        // Get the component and convert to legacy string
        @Nullable VersionedComponent name = displayName();
        if (name == null) { return null; }
        return name.serializeLegacySection();
    }

    /**
     * Get the custom display name for the item.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * If the patch name is null (not set), the prototype's name will be returned (if available).<br>
     */
    @Nullable
    default VersionedComponent displayName() {
        return customName();
    }

    /**
     * Get the custom display name for the item.<br>
     * Placeholders are set automatically via {@link SoftPlaceholderAPI#setPlaceholders(OfflinePlayer, String)}.<br>
     * <br>
     * If the patch name is null (not set), the prototype's name will be returned (if available).<br>
     */
    @Nullable
    VersionedComponent customName();

    /**
     * Get the custom lore for the item.<br>
     * <br>
     * If the patch lore is null (not set), the prototype's lore will be returned (if available).
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #lore()} which returns components.
     */
    @Deprecated
    @Nullable
    default List<@NotNull String> getLore() {
        // Get the components and convert to legacy strings
        @Nullable List<VersionedComponent> lore = lore();
        if (lore == null) { return null; }
        return lore.stream().map(VersionedComponent::serializeLegacySection).toList();
    }

    /**
     * Get the custom lore for the item.<br>
     * <br>
     * If the patch lore is null (not set), the prototype's lore will be returned (if available).
     */
    @Nullable
    List<@NotNull VersionedComponent> lore();

    /**
     * Get if the item is unbreakable or not.<br>
     * <br>
     * If the patch unbreakable is null (not set), the prototype's unbreakable value will be returned.
     */
    boolean isUnbreakable();

    /**
     * Get if the item has the specified item flag.<br>
     * <br>
     * If the patch does not specify the presence of this flag, the prototype's value will be returned.
     */
    boolean hasItemFlag(@NotNull XItemFlag flag);

    /**
     * Get a set of ALL item flags on the item.<br>
     * <br>
     * This is the combination of the prototype's item flags and the patch item flag overrides.<br>
     * This set is never null, but can be empty if no item flags are present on the prototype or patch.
     */
    @NotNull
    Set<XItemFlag> getItemFlags();

    /**
     * Get the level of an enchantment on the item, or 0 if the item does not have this enchantment.<br>
     * <br>
     * If the patch does not specify a level for this enchantment, the prototype's value will be returned.
     */
    int getEnchantmentLevel(@NotNull XEnchantment enchant);

    /**
     * Get a map of ALL enchantments on the item.<br>
     * <br>
     * This is the combination of the prototype's enchantments and the patch enchantment overrides.<br>
     * This map is never null, but can be empty if no enchantments are present on the prototype or patch.
     */
    @NotNull
    Map<XEnchantment, Integer> getEnchantments();

    /**
     * Get if the item has an added glow effect.<br>
     * <br>
     * If the patch glow is null (not set), the prototype's glow value will be returned.
     */
    boolean hasGlow();

    /**
     * Alias of {@link #hasGlow()}.
     */
    default boolean isAddGlow() {
        return hasGlow();
    }

    /**
     * Get the owner of a player head item, or null if not set / not a player head.<br>
     * <br>
     * NOTE: This will always return null if {@link #willUseSkullOwner()} returns false.
     */
    @Nullable
    String getSkullOwner();

    /**
     * Check if the prototype item supports setting a skull owner (is a player head).<br>
     * <br>
     * If this returns true, then the following methods will modify the item:<br>
     * - {@link #setSkullOwner(String)}<br>
     * - {@link #resetSkullOwner()}<br>
     * - {@link #getSkullOwner()}<br>
     * If this returns false, calling those methods have no effect and {@link #getSkullOwner()} will always return null.
     */
    boolean willUseSkullOwner();



    // ------------------------------------------------------------ //
    //                             BUILD                            //
    // ------------------------------------------------------------ //
    /**
     * Compiles the patches, applying them on top of the prototype.
     * @return The final {@link ItemStack} with all patches applied.
     */
    @NotNull
    default ItemStack build() {
        return build(null);
    }

    /**
     * Compiles the patches, applying them on top of the prototype.
     * @param viewer The {@link Player} viewing this item, for PlaceholderAPI support. (Can be null)
     * @return The final {@link ItemStack} with all patches applied.
     */
    @NotNull
    ItemStack build(@Nullable Player viewer);

    /**
     * Alias of {@link #build()}.
     * @return The final {@link ItemStack} with all patches applied.
     */
    @NotNull
    default ItemStack toItemStack() {
        return build();
    }

    /**
     * Alias of {@link #build(Player)}.
     * @param player The {@link Player} viewing this item, for PlaceholderAPI support. (Can be null)
     * @return The final {@link ItemStack} with all patches applied.
     */
    @NotNull
    default ItemStack toItemStack(@Nullable Player player) {
        return build(player);
    }



    // ------------------------------------------------------------ //
    //                    PATCH PROPERTY HELPERS                    //
    // ------------------------------------------------------------ //
    /**
     * PATCH PROPERTY HELPER - Replaces all occurrences of a substring in the name with another string.<br>
     * This transformation only applies to the name patch, if no name patch is set this does nothing.<br>
     * It does NOT modify the prototype's name.
     * @param find The substring to find
     * @param replacement The sub
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #replaceName(String, VersionedComponent)} which supports components.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T replaceName(@NotNull String find, @NotNull String replacement) {
        Preconditions.checkNotNull(find, "Find string cannot be null");
        Preconditions.checkNotNull(replacement, "Replacement string cannot be null");
        // Convert to component and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        return this.replaceName(find, serializer.fromLegacySection(LegacyColors.t(replacement)));
    }

    /**
     * PATCH PROPERTY HELPER - Replaces all occurrences of a substring in the name with another component.<br>
     * This transformation only applies to the name patch, if no name patch is set this does nothing.<br>
     * It does NOT modify the prototype's name.
     * <br>
     * It will serialize the name into message format, perform the replacement, then deserialize it back to a component.
     *
     * @param find The substring to find
     * @param replacement The component to replace each occurrence with
     * @return This builder, for chaining
     */
    @NotNull
    T replaceName(@NotNull String find, @NotNull VersionedComponent replacement);

    /**
     * Alias of {@link #replaceNamePAPI(OfflinePlayer)} with a {@code null} player.
     */
    @NotNull
    default T replaceNamePAPI() {
        return replaceNamePAPI(null);
    }

    /**
     * PATCH PROPERTY HELPER - Replaces all PlaceholderAPI placeholders in the name with their respective values.<br>
     * This transformation only applies to the name patch, if no name patch is set this does nothing.<br>
     * It does NOT modify the prototype's name.
     *
     * @param player The player to use for PlaceholderAPI replacements, or null to skip player-specific placeholders
     * @return This builder, for chaining
     */
    @NotNull
    T replaceNamePAPI(@Nullable OfflinePlayer player);

    /**
     * PATCH PROPERTY HELPER - Searches for a substring in the lore and replaces that entire line with replacement lines.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.<br>
     * <br>
     * For each line, it checks if the component plain text OR the component mini message contains the find string.<br>
     * If either matches, that line is substituted with the replacement lines.
     *
     * @param find The string to search for in the lore (color codes will be stripped for comparison)
     * @param replacements The lines to swap in, in place of the entire line containing the find string
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #replaceLoreLineComponent(String, List)} which supports components.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T replaceLoreLine(@NotNull String find, @NotNull List<@NotNull String> replacements) {
        Preconditions.checkNotNull(find, "Find string cannot be null");
        Preconditions.checkNotNull(replacements, "Replacement lines cannot be null");
        // Convert to components and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        List<VersionedComponent> components = new ArrayList<>(replacements.size());
        for (String line : replacements) {
            components.add(serializer.fromLegacySection(LegacyColors.t(line)));
        }
        return this.replaceLoreLineComponent(find, components);
    }

    /**
     * PATCH PROPERTY HELPER - Searches for a substring in the lore and replaces that entire line with replacement lines.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.<br>
     * <br>
     * For each line, it checks if the component plain text OR the component mini message contains the find string.<br>
     * If either matches, that line is substituted with the replacement lines.
     *
     * @param find The string to search for in the lore
     * @param replacements The lines to swap in, in place of the entire line containing the find string
     * @return This builder, for chaining
     */
    @NotNull
    default T replaceLoreLineComponent(@NotNull String find, @NotNull List<@NotNull VersionedComponent> replacements) {
        Predicate<VersionedComponent> defaultFilter = line ->
                line.serializePlainText().contains(find) || line.serializeMiniMessage().contains(find);
        return replaceLoreLineComponent(defaultFilter, replacements);
    }

    /**
     * PATCH PROPERTY HELPER - Replaces all occurrences of lines matching the given predicate in the lore with replacement lines.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.<br>
     * <br>
     * For each line, it checks if the predicate returns true, and if so that line is replaced with the replacement lines.
     *
     * @param filter The predicate to test each lore line against, if it returns true that line is replaced
     * @param replacements The lines to swap in, in place of the entire line containing the find string
     * @return This builder, for chaining
     */
    @NotNull
    T replaceLoreLineComponent(@NotNull Predicate<@NotNull VersionedComponent> filter, @NotNull List<@NotNull VersionedComponent> replacements);

    /**
     * PATCH PROPERTY HELPER - Replaces all occurrences of a substring in each lore line with another string.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.
     *
     * @param find The substring to find in each lore line
     * @param replacement The string to replace each occurrence with
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #replaceLore(String, VersionedComponent)} which supports components.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T replaceLore(@NotNull String find, @NotNull String replacement) {
        Preconditions.checkNotNull(find, "Find string cannot be null");
        Preconditions.checkNotNull(replacement, "Replacement string cannot be null");
        // Convert to component and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        VersionedComponent replacementComp = serializer.fromLegacySection(LegacyColors.t(replacement));
        return this.replaceLore(find, replacementComp);
    }

    /**
     * PATCH PROPERTY HELPER - Replaces all occurrences of a substring in each lore line with another component.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.<br>
     * <br>
     * It will serialize each lore line into mini message format, perform the replacement, then deserialize it back to a component.
     *
     * @param find The substring to find in each lore line
     * @param replacement The component to replace each occurrence with
     * @return This builder, for chaining
     */
    T replaceLore(@NotNull String find, @NotNull VersionedComponent replacement);

    /**
     * Alias of {@link #replaceLorePAPI(OfflinePlayer)} with a {@code null} player.
     */
    @NotNull
    default T replaceLorePAPI() {
        return replaceLorePAPI(null);
    }

    /**
     * PATCH PROPERTY HELPER - Replaces all PlaceholderAPI placeholders in the lore with their respective values.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.
     *
     * @param player The player to use for PlaceholderAPI replacements, or null to skip player-specific placeholders
     * @return This builder, for chaining
     */
    @NotNull
    T replaceLorePAPI(@Nullable OfflinePlayer player);

    /**
     * PATCH PROPERTY HELPER - Convenience method to replace a substring in both name and lore.<br>
     * This is equivalent to calling {@link #replaceName(String, String)} followed by {@link #replaceLore(String, String)}.<br>
     * <br>
     * This transformation only applies to existing patches, if no name or lore patches are set those won't be affected.<br>
     * It does NOT modify the prototype's name or lore.
     *
     * @param find The substring to find in name and lore
     * @param replacement The string to replace each occurrence with
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #replaceBoth(String, VersionedComponent)} which supports components.
     * @return This builder, for chaining
     */
    @Deprecated
    @NotNull
    default T replaceBoth(@NotNull String find, @NotNull String replacement) {
        Preconditions.checkNotNull(find, "Find string cannot be null");
        Preconditions.checkNotNull(replacement, "Replacement string cannot be null");
        // Convert to component and use proper method
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        VersionedComponent replacementComp = serializer.fromLegacySection(LegacyColors.t(replacement));
        return replaceName(find, replacementComp).replaceLore(find, replacementComp);
    }

    /**
     * PATCH PROPERTY HELPER - Convenience method to replace a substring in both name and lore.<br>
     * This is equivalent to calling {@link #replaceName(String, VersionedComponent)} followed by {@link #replaceLore(String, VersionedComponent)}.<br>
     * <br>
     * This transformation only applies to existing patches, if no name or lore patches are set those won't be affected.<br>
     * It does NOT modify the prototype's name or lore.
     *
     * @param find The substring to find in name and lore
     * @param replacement The component to replace each occurrence with
     * @return This builder, for chaining
     */
    @NotNull
    default T replaceBoth(@NotNull String find, @NotNull VersionedComponent replacement) {
        Preconditions.checkNotNull(find, "Find string cannot be null");
        Preconditions.checkNotNull(replacement, "Replacement component cannot be null");
        return replaceName(find, replacement).replaceLore(find, replacement);
    }

    /**
     * Alias of {@link #replaceBothPAPI(OfflinePlayer)} with a {@code null} player.
     */
    @NotNull
    default T replaceBothPAPI() {
        return replaceBothPAPI(null);
    }

    /**
     * PATCH PROPERTY HELPER - Convenience method to replace PlaceholderAPI placeholders in both name and lore.<br>
     * This is equivalent to calling {@link #replaceNamePAPI(OfflinePlayer)} followed by {@link #replaceLorePAPI(OfflinePlayer)}.<br>
     * <br>
     * This transformation only applies to existing patches, if no name or lore patches are set those won't be affected.<br>
     * It does NOT modify the prototype's name or lore.
     *
     * @param player The player to use for PlaceholderAPI replacements, or null to skip player-specific placeholders
     * @return This builder, for chaining
     */
    @NotNull
    default T replaceBothPAPI(@Nullable OfflinePlayer player) {
        return replaceNamePAPI(player).replaceLorePAPI(player);
    }

    // ------------------------------------------------------------ //
    //                            CLONING                           //
    // ------------------------------------------------------------ //
    @NotNull ItemBuilder clone();

    /**
     * Create a clone of this builder with all the same patches, but a different prototype {@link ItemStack}.
     * @since 5.0.0-alpha.17
     */
    @NotNull ItemBuilder cloneWithNewPrototype(@NotNull ItemStack newPrototype);

    /**
     * Create a clone of this builder with all the same patches, but a different prototype {@link ItemStack}.<br>
     * This method uses {@link XMaterial#parseItem()} to convert the {@link XMaterial} to an {@link ItemStack}.
     * @since 5.0.0-alpha.17
     */
    default @NotNull ItemBuilder cloneWithNewPrototype(@NotNull XMaterial newPrototype) {
        ItemStack stack = Preconditions.checkNotNull(
                Preconditions.checkNotNull(newPrototype, "XMaterial cannot be null").parseItem(),
                "XMaterial " + newPrototype.name() + " could not be parsed to a valid ItemStack!"
        );
        return cloneWithNewPrototype(stack);
    }

    /**
     * Create a clone of this builder with all the same patches, but a different prototype {@link ItemStack}.<br>
     * This method uses {@link XMaterial#matchXMaterial(Material)} to convert the {@link Material} to an {@link XMaterial},<br>
     * then uses {@link XMaterial#parseItem()} to convert that to an {@link ItemStack}.
     * @since 5.0.0-alpha.17
     */
    default @NotNull ItemBuilder cloneWithNewPrototype(@NotNull Material newPrototype) {
        XMaterial xMat = Preconditions.checkNotNull(
                XMaterial.matchXMaterial(Preconditions.checkNotNull(newPrototype, "Material cannot be null")),
                "Material " + newPrototype.name() + " could not be matched to a valid XMaterial!"
        );
        ItemStack stack = Preconditions.checkNotNull(
                Preconditions.checkNotNull(xMat.parseItem(), "XMaterial " + xMat.name() + " could not be parsed to a valid ItemStack!"),
                "XMaterial " + xMat.name() + " could not be parsed to a valid ItemStack!"
        );
        return cloneWithNewPrototype(stack);
    }
}
