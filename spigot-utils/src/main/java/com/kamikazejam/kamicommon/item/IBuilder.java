package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.util.Preconditions;
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
     * PATCH FUNCTION - Applies a custom display name for the item.<br>
     * No color translations are applied, process them BEFORE setting the name.<br>
     * <br>
     * Clear this patch by calling {@link #resetName()}.
     * @return This builder, for chaining
     */
    @NotNull
    T setName(@NotNull String name);

    /**
     * Alias of {@link #setName(String)}.
     */
    @NotNull
    default T setDisplayName(@NotNull String name) {
        return setName(name);
    }

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
     * No color translations are applied, process them BEFORE setting the lore.<br>
     * <br>
     * Clear this patch by calling {@link #resetLore()}.
     * @return This builder, for chaining
     */
    @NotNull
    T setLore(@NotNull String... line);

    /**
     * PATCH FUNCTION - Sets the custom lore for the item.<br>
     * No color translations are applied, process them BEFORE setting the lore.<br>
     * <br>
     * Clear this patch by calling {@link #resetLore()}.
     * @return This builder, for chaining
     */
    @NotNull
    T setLore(@NotNull List<@NotNull String> lore);

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
        return setLore(new ArrayList<>());
    }

    /**
     * PATCH FUNCTION - Appends additional lines to the current lore.<br>
     * If no lore patch is currently set, this will create a new lore patch with the provided lines.<br>
     * If a lore patch is already set, the new lines will be added to the end of the existing lore.<br>
     * <br>
     * No color translations are applied, process them BEFORE adding the lines.
     * @param lines The lore lines to append
     * @return This builder, for chaining
     */
    @NotNull
    T addLoreLines(@NotNull List<@NotNull String> lines);

    /**
     * PATCH FUNCTION - Appends additional lines to the current lore.<br>
     * If no lore patch is currently set, this will create a new lore patch with the provided lines.<br>
     * If a lore patch is already set, the new lines will be added to the end of the existing lore.<br>
     * <br>
     * No color translations are applied, process them BEFORE adding the lines.
     * @param lines The lore lines to append
     * @return This builder, for chaining
     */
    @NotNull
    default T addLoreLines(@NotNull String... lines) {
        return addLoreLines(Arrays.asList(lines));
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
     * No color translations are applied, process them AFTER getting the name.<br>
     * <br>
     * If the patch name is null (not set), the prototype's name will be returned (if available).
     */
    @Nullable
    String getName();

    /**
     * Get the custom lore for the item.<br>
     * <br>
     * If the patch lore is null (not set), the prototype's lore will be returned (if available).
     */
    @Nullable
    List<@NotNull String> getLore();

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
     * Get the level of an enchantment on the item, or 0 if the item does not have this enchantment.<br>
     * <br>
     * If the patch does not specify a level for this enchantment, the prototype's value will be returned.
     */
    int getEnchantmentLevel(@NotNull XEnchantment enchant);

    /**
     * Get if the item has an added glow effect.<br>
     * <br>
     * If the patch glow is null (not set), the prototype's glow value will be returned.
     */
    boolean hasGlow();

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
     * @return This builder, for chaining
     */
    @NotNull
    T replaceName(@NotNull String find, @NotNull String replacement);

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
     * Uses {@link org.bukkit.ChatColor#stripColor(String)} for comparison to ignore color formatting.
     *
     * @param find The string to search for in the lore (color codes will be stripped for comparison)
     * @param replacement The lines to swap in, in place of the entire line containing the find string
     * @return This builder, for chaining
     */
    @NotNull
    T replaceLoreLine(@NotNull String find, @NotNull List<@NotNull String> replacement);

    /**
     * PATCH PROPERTY HELPER - Replaces all occurrences of a substring in each lore line with another string.<br>
     * This transformation only applies to the lore patch, if no lore patch is set this does nothing.<br>
     * It does NOT modify the prototype's lore.
     *
     * @param find The substring to find in each lore line
     * @param replacement The string to replace each occurrence with
     * @return This builder, for chaining
     */
    @NotNull
    T replaceLore(@NotNull String find, @NotNull String replacement);

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
     * @return This builder, for chaining
     */
    @NotNull
    default T replaceBoth(@NotNull String find, @NotNull String replacement) {
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
