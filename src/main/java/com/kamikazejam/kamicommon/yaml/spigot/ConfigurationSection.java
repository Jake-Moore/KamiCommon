package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.yaml.base.ConfigurationMethods;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public interface ConfigurationSection extends ConfigurationMethods<ConfigurationSection> {
    ItemStack getItemStack(String key);
    ItemStack getItemStack(String key, ItemStack def);
    void setItemStack(String key, ItemStack item);


    void setItemBuilder(String key, IBuilder builder);
    IBuilder getItemBuilder(String key);
}
