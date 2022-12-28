package com.kamikazejamplugins.kamicommon.yaml.bukkit;

import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class ItemStackHelper {
    private final ConfigurationSection section;
    public ItemStackHelper(ConfigurationSection section) {
        this.section = section;
    }

    public boolean isStack(Object obj) {
        return obj instanceof ItemStack;
    }

    public void setItemStack(String key, Object item) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", item);
        String stringData = config.saveToString();
        section.setString(key, stringData);
    }

    public ItemStack getItemStack(String key) {
        try {
            String stringData = section.getString(key);
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(stringData);
            return config.getItemStack("item");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack getItemStack(String key, Object def) {
        if (section.contains(key)) { return getItemStack(key);
        }else { return (ItemStack) def; }
    }
}
