package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.nodes.MappingNode;

import javax.annotation.Nullable;

@Getter
@SuppressWarnings("unused")
public class MemorySection extends MemorySectionMethods<MemorySection> implements ConfigurationSection {
    public MemorySection(@Nullable MappingNode node) {
        super(node);
    }

    @Override
    public MemorySection getConfigurationSection(String key) {
        Object o = get(key);
        if (o instanceof MappingNode) {
            MappingNode m = (MappingNode) o;
            return new MemorySection(m);
        }
        return new MemorySection(AbstractYamlHandler.createNewMappingNode());
    }

    @Override
    public void set(String key, Object value) { put(key, value); }
    @Override
    public void put(String key, Object value) {
        // ItemStacks
        if (value instanceof ItemStack) {
            ItemStack stack = (ItemStack) value;
            setItemStack(key, stack); return;
        }

        // ItemBuilders
        if (value instanceof IBuilder) {
            IBuilder builder = (IBuilder) value;
            setItemBuilder(key, builder); return;
        }

        super.put(key, value);
    }



    /**
     * Supported in Spigot-Backed Config classes, you must cast to ItemStack if return is not null.
     * @return the ItemStack at the given key, or null if it doesn't exist
     */
    @Override
    public ItemStack getItemStack(String key) {
        try {
            String stringData = getString(key);
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(stringData);
            return config.getItemStack("item");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ItemStack getItemStack(String key, ItemStack def) {
        if (contains(key)) { return getItemStack(key); }
        return def;
    }

    @Override
    public void setItemStack(String key, ItemStack stack) {
        if (stack == null) { set(key, null); return; }

        YamlConfiguration config = new YamlConfiguration();
        config.set("item", stack);
        String stringData = config.saveToString();
        setString(key, stringData);
    }

    @Override
    public void setItemBuilder(String key, IBuilder builder) {
        if (builder == null) { set(key, null); return; }
        setItemStack(key, builder.toItemStack());
    }

    @Override
    public IBuilder getItemBuilder(String key) {
        ItemStack stack = getItemStack(key);
        if (stack == null) { return null; }
        return new ItemBuilder(stack);
    }

}
