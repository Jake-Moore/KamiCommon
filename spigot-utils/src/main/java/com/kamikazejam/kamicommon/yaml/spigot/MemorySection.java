package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.ConfigurationMethods;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

@Getter
@SuppressWarnings("unused")
public class MemorySection extends MemorySectionMethods<MemorySection> implements ConfigurationSection {
    @Getter(AccessLevel.NONE)
    private final @NotNull String fullPath;
    public MemorySection(@Nullable MappingNode node, @NotNull String fullPath, @Nullable ConfigurationMethods<?> parent) {
        super(node, parent);
        this.fullPath = fullPath;
    }

    @Override
    public @NotNull MemorySection getConfigurationSection(String key) {
        Object o = get(key);
        String newPath = (this.fullPath.isEmpty()) ? key : this.fullPath + "." + key;
        if (o instanceof MappingNode m) {
            return new MemorySection(m, newPath, this);
        }
        return new MemorySection(AbstractYamlHandler.createNewMappingNode(), newPath, this);
    }

    @Override
    public @NotNull ConfigurationSequenceSpigot getConfigurationSequence(String key) {
        @Nullable Node node = getNode(key);
        String newPath = (this.fullPath.isEmpty()) ? key : this.fullPath + "." + key;

        if (node instanceof SequenceNode sequenceNode) {
            return new ConfigurationSequenceSpigot(this, sequenceNode, newPath);
        }

        // Return empty sequence if not found or not a sequence
        return new ConfigurationSequenceSpigot(this, null, newPath);
    }

    @Override
    public void set(String key, Object value) { put(key, value); }
    @Override
    public void put(String key, Object value) {
        // ItemStacks
        if (value instanceof ItemStack stack) {
            setItemStack(key, stack); return;
        }

        // ItemBuilders
        if (value instanceof ItemBuilder builder) {
            setItemStack(key, builder.build()); return;
        }

        super.put(key, value);
    }

    @Override
    public Object get(String key) {
        Object o = super.get(key);

        // Check ItemStack logic
        if (o instanceof String s) {
            ItemStack stack = parseItemStackData(s);
            if (stack != null) { return stack; }
        }

        // Otherwise return the default value
        return o;
    }
    private @Nullable ItemStack parseItemStackData(@Nullable String stringData) {
        if (stringData == null) { return null; }

        try {
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(stringData);
            return config.getItemStack("item");
        }catch (Throwable ignored) {}
        return null;
    }


    /**
     * Supported in Spigot-Backed Config classes, you must cast to ItemStack if return is not null.
     * @return the ItemStack at the given key, or null if it doesn't exist
     */
    @Override
    public ItemStack getItemStack(String key) {
        return getItemStack(key, null);
    }

    @Override
    public ItemStack getItemStack(String key, ItemStack def) {
        if (!contains(key)) { return def; }

        Object o = get(key);
        if (o instanceof ItemStack) { return (ItemStack) o; }
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
    public boolean isItemStack(String key) {
        if (!contains(key)) { return false; }
        ItemStack stack = getItemStack(key);
        return stack != null;
    }

    @Override
    public @NotNull ItemBuilder parseItemBuilder(@NotNull String key) {
        MemorySection section = getConfigurationSection(key);
        return ItemBuilder.load(section);
    }

    @Override
    public String getCurrentPath() {
        return this.fullPath;
    }
}
