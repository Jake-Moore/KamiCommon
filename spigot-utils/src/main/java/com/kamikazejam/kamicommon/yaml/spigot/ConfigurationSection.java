package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilderLoader;
import com.kamikazejam.kamicommon.yaml.base.ConfigurationMethods;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ConfigurationSection extends ConfigurationMethods<ConfigurationSection> {
    ItemStack getItemStack(String key);
    ItemStack getItemStack(String key, ItemStack def);
    void setItemStack(String key, ItemStack item);

    boolean isItemStack(String key);

    String getCurrentPath();

    /**
     * Parses the subsection at the given key into an {@link ItemBuilder}. This is equivalent to using:<br>
     * - {@link ItemBuilderLoader#load(ConfigurationSection)} with the subsection at the given key as the argument.
     * @param key The key of the subsection to parse.
     * @return The parsed {@link ItemBuilder}.
     * @throws IllegalArgumentException if the parsing fails. (see {@link ItemBuilderLoader#load(ConfigurationSection)} for more details)
     * @since 5.0.0-alpha.17
     */
    @NotNull ItemBuilder parseItemBuilder(@NotNull String key);
}
