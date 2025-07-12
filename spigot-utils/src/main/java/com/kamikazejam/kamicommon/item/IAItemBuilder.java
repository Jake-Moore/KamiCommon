package com.kamikazejam.kamicommon.item;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;

import dev.lone.itemsadder.api.CustomStack;

@SuppressWarnings({"unused", "UnreachableCode"})
public class IAItemBuilder extends IBuilder {

    private IAItemBuilder() {} // Private for Clone
    public IAItemBuilder(@NotNull ConfigurationSection section) {
        super(section);
    }
    public IAItemBuilder(@NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        super(section, offlinePlayer);
    }
    public IAItemBuilder(@NotNull XMaterial mat, @NotNull ConfigurationSection section) {
        super(mat, section);
    }
    public IAItemBuilder(@NotNull XMaterial mat, @NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        super(mat, section, offlinePlayer);
    }
    public IAItemBuilder(@NotNull ItemStack base, @NotNull ConfigurationSection section) {
        super(base, section);
    }
    public IAItemBuilder(@NotNull ItemStack base, @NotNull ConfigurationSection section, @Nullable OfflinePlayer offlinePlayer) {
        super(base, section, offlinePlayer);
    }
    public IAItemBuilder(@NotNull XMaterial m) {
        super(m);
    }
    public IAItemBuilder(@NotNull XMaterial m, short damage) {
        super(m, damage);
    }
    public IAItemBuilder(@NotNull XMaterial m, int amount) {
        super(m, amount);
    }
    public IAItemBuilder(@NotNull XMaterial material, int amount, short damage) {
        super(material, amount, damage);
    }
    public IAItemBuilder(@NotNull ItemStack is) {
        super(is);
    }
    public IAItemBuilder(@NotNull ItemStack is, boolean clone) {
        super(is, clone);
    }
    public IAItemBuilder(@NotNull String namespacedID) {
        this(namespacedID, 1);
    }
    public IAItemBuilder(@NotNull String namespacedID, int amount) {
        CustomStack stack = CustomStack.getInstance(namespacedID);
        if (stack == null) {
            throw new IllegalArgumentException("Invalid CustomStack namespacedID: " + namespacedID);
        }
        this.setBase(stack.getItemStack());
    }

    public IAItemBuilder(@NotNull String matOrNamespacedID, @NotNull ConfigurationSection section) {
        this(matOrNamespacedID, section, null);
    }
    public IAItemBuilder(@NotNull String matOrNamespacedID, @NotNull ConfigurationSection section, @Nullable OfflinePlayer player) {
        // Empty super constructor, all data will be loaded here
        super();
        if (player != null) {
            this.setSkullOwner(player.getName());
        }

        try {
            // Try the string as a Material
            this.setMaterial(parseXMaterial(matOrNamespacedID));
        }catch (IllegalArgumentException ignored) {
            // Try the string as a namespacedID
            CustomStack stack = CustomStack.getInstance(matOrNamespacedID);
            if (stack == null) {
                throw new IllegalArgumentException("Invalid material or namespacedID: " + matOrNamespacedID);
            }
            this.setBase(stack.getItemStack());
        }
        this.loadConfigItem(section, player, false);
    }

    @Override
    public void loadTypes(ConfigurationSection config) {
        // Try Loading an XMaterial
        if (this.loadXMaterial(config)) { return; }

        // Try Loading a CustomStack
        @Nullable String id = config.getString("material", config.getString("type", null));
        if (id == null) {
            throw new IllegalStateException("No materials/namespacedIDs found in config for section: " + config.getCurrentPath());
        }
        CustomStack stack = CustomStack.getInstance(id);
        if (stack == null) {
            throw new IllegalStateException("Invalid CustomStack namespacedID: " + id);
        }
        this.setBase(stack.getItemStack());
    }

    @Override
    public IBuilder clone() {
        return loadClone(new IAItemBuilder());
    }
}
