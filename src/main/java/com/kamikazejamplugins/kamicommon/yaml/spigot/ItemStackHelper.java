package com.kamikazejamplugins.kamicommon.yaml.spigot;

import com.kamikazejamplugins.kamicommon.item.IBuilder;
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

    public boolean isBuilder(Object obj) {
        return obj instanceof IBuilder;
    }

    public void setItemStack(String key, Object item) {
        if (item instanceof ItemStack) { setItemStack(key, (ItemStack) item); }
        throw new IllegalArgumentException("Object is not an ItemStack");
    }

    public void setItemBuilder(String key, Object builder) {
        if (builder instanceof IBuilder) { setItemStack(key, ((IBuilder) builder).toItemStack()); }
        throw new IllegalArgumentException("Object is not an IBuilder");
    }

    private void setItemStack(String key, ItemStack stack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", stack);
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
