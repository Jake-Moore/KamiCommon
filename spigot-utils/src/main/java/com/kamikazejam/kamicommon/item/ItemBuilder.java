package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ItemBuilder extends IBuilder {

    public ItemBuilder(ConfigurationSection section) {
        super(section);
    }
    public ItemBuilder(ConfigurationSection section, OfflinePlayer offlinePlayer) {
        super(section, offlinePlayer);
    }
    public ItemBuilder(XMaterial m) {
        super(m);
    }
    public ItemBuilder(int id) {
        super(id);
    }
    public ItemBuilder(int id, short damage) {
        super(id, damage);
    }
    public ItemBuilder(XMaterial m, short damage) {
        super(m, damage);
    }
    public ItemBuilder(int id, int amount) {
        super(id, amount);
    }
    public ItemBuilder(XMaterial m, int amount) {
        super(m, amount);
    }
    public ItemBuilder(int id, int amount, short damage) {
        super(id, amount, damage);
    }
    public ItemBuilder(XMaterial material, int amount, short damage) {
        super(material, amount, damage);
    }
    public ItemBuilder(ItemStack is) {
        super(is);
    }
    public ItemBuilder(ItemStack is, boolean clone) {
        super(is, clone);
    }

    @Override
    public void loadBasicItem(ConfigurationSection config) {
        short damage = (short) config.getInt("damage", 0);
        int amount = config.getInt("amount", 1);

        String mat = config.getString("material", config.getString("type", null));
        if (mat != null) {
            this.material = XMaterial.matchXMaterial(mat).orElseThrow(() -> new IllegalArgumentException("Invalid material: " + config.getString("material")));
        }
        this.amount = amount;
        this.damage = damage;
        this.name = config.getString("name");
        this.lore = config.getStringList("lore");
    }

    @Override
    public void loadPlayerHead(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer) {
        loadBasicItem(config);
        if (offlinePlayer != null) {
            this.skullOwner = offlinePlayer.getName();
        }
    }

    @Override
    public IBuilder clone() {
        return loadClone(new ItemBuilder(this.material, this.amount, this.damage));
    }
}
