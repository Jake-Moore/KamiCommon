package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ItemBuilder extends IBuilder {

    ItemStack base = null;
    private final @NotNull List<XMaterial> materialCycle = new ArrayList<>();

    private ItemBuilder() {} // Private for Clone
    public ItemBuilder(@NotNull ConfigurationSection section) {
        super(section);
    }
    public ItemBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        super(section, offlinePlayer);
    }
    public ItemBuilder(@NotNull XMaterial mat, @NotNull ConfigurationSection section) {
        super(mat, section);
    }
    public ItemBuilder(@NotNull XMaterial mat, @NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        super(mat, section, offlinePlayer);
    }
    public ItemBuilder(@NotNull ItemStack base, @NotNull ConfigurationSection section) {
        super(base, section);
    }
    public ItemBuilder(@NotNull ItemStack base, @NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        super(base, section, offlinePlayer);
    }
    public ItemBuilder(@NotNull XMaterial m) {
        super(m);
    }
    public ItemBuilder(@NotNull XMaterial m, short damage) {
        super(m, damage);
    }
    public ItemBuilder(@NotNull XMaterial m, int amount) {
        super(m, amount);
    }
    public ItemBuilder(@NotNull XMaterial material, int amount, short damage) {
        super(material, amount, damage);
    }
    public ItemBuilder(@NotNull ItemStack is) {
        super(is);
    }
    public ItemBuilder(@NotNull ItemStack is, boolean clone) {
        super(is, clone);
    }

    @Override
    public void loadTypes(ConfigurationSection config) {
        // Require Loading a XMaterial
        if (this.loadXMaterial(config)) { return; }
        throw new IllegalStateException("No materials found in config.");
    }

    @Override
    public IBuilder clone() {
        return loadClone(new ItemBuilder());
    }
}
