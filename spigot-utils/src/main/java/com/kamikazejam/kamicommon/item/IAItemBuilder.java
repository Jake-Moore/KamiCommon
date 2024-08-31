package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnreachableCode"})
public class IAItemBuilder extends IBuilder {

    private IAItemBuilder() {} // Private for Clone
    public IAItemBuilder(ConfigurationSection section) {
        super(section);
    }
    public IAItemBuilder(ConfigurationSection section, OfflinePlayer offlinePlayer) {
        super(section, offlinePlayer);
    }
    public IAItemBuilder(XMaterial mat, ConfigurationSection section) {
        super(mat, section);
    }
    public IAItemBuilder(XMaterial mat, ConfigurationSection section, OfflinePlayer offlinePlayer) {
        super(mat, section, offlinePlayer);
    }
    public IAItemBuilder(ItemStack base, ConfigurationSection section) {
        super(base, section);
    }
    public IAItemBuilder(ItemStack base, ConfigurationSection section, OfflinePlayer offlinePlayer) {
        super(base, section, offlinePlayer);
    }
    public IAItemBuilder(XMaterial m) {
        super(m);
    }
    public IAItemBuilder(XMaterial m, short damage) {
        super(m, damage);
    }
    public IAItemBuilder(XMaterial m, int amount) {
        super(m, amount);
    }
    public IAItemBuilder(XMaterial material, int amount, short damage) {
        super(material, amount, damage);
    }
    public IAItemBuilder(ItemStack is) {
        super(is);
    }
    public IAItemBuilder(ItemStack is, boolean clone) {
        super(is, clone);
    }
    public IAItemBuilder(String namespacedID) {
        this(namespacedID, 1);
    }
    public IAItemBuilder(String namespacedID, int amount) {
        CustomStack stack = CustomStack.getInstance(namespacedID);
        this.setBase(stack.getItemStack());
    }

    public IAItemBuilder(String matOrNamespacedID, ConfigurationSection section) {
        this(matOrNamespacedID, section, null);
    }
    public IAItemBuilder(String matOrNamespacedID, ConfigurationSection section, @Nullable OfflinePlayer player) {
        // Empty super constructor, all data will be loaded here
        super();
        if (player != null) {
            this.setSkullOwner(player.getName());
        }

        try {
            // Try the string as a Material
            this.setMaterial(parseMaterial(matOrNamespacedID));
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
