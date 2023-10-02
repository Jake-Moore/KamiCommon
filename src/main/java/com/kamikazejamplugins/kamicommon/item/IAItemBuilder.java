package com.kamikazejamplugins.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue", "FieldCanBeLocal", "DuplicatedCode"})
public class IAItemBuilder extends IBuilder {

    public IAItemBuilder(ConfigurationSection section) {
        super(section);
    }
    public IAItemBuilder(ConfigurationSection section, OfflinePlayer offlinePlayer) {
        super(section, offlinePlayer);
    }
    public IAItemBuilder(XMaterial m) {
        super(m);
    }
    public IAItemBuilder(int id) {
        super(id);
    }
    public IAItemBuilder(int id, short damage) {
        super(id, damage);
    }
    public IAItemBuilder(XMaterial m, short damage) {
        super(m, damage);
    }
    public IAItemBuilder(int id, int amount) {
        super(id, amount);
    }
    public IAItemBuilder(XMaterial m, int amount) {
        super(m, amount);
    }
    public IAItemBuilder(int id, int amount, short damage) {
        super(id, amount, damage);
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
        super((ItemStack) null);

        CustomStack stack = CustomStack.getInstance(namespacedID);
        this.base = stack.getItemStack();
    }

    @Override
    public void loadBasicItem(ConfigurationSection config) {
        this.damage = (short) config.getInt("damage", 0);
        this.amount = config.getInt("amount", 1);

        this.name = config.getString("name");
        this.lore = config.getStringList("lore");

        String mat = config.getString("material", config.getString("type"));
        CustomStack customStack = CustomStack.getInstance(mat);

        ItemStack item;
        if (customStack != null) {
            this.base = customStack.getItemStack();
        }else {
            this.material = XMaterial.matchXMaterial(config.getString("material", config.getString("type"))).orElseThrow(() -> new IllegalArgumentException("Invalid material: " + config.getString("material", config.getString("type"))));
        }
    }

    @Override
    public void loadPlayerHead(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer) {
        loadBasicItem(config);
        // Set the skull owner if it's not null
        if (offlinePlayer != null) {
            this.skullOwner = offlinePlayer.getName();
        }
    }

    @Override
    public IBuilder clone() {

        IAItemBuilder itemBuilder = new IAItemBuilder(this.material, this.amount, this.damage);
        itemBuilder.name = name;
        itemBuilder.lore = lore;
        itemBuilder.unbreakable = unbreakable;
        itemBuilder.itemFlags = itemFlags;
        itemBuilder.enchantments = enchantments;
        itemBuilder.addGlow = addGlow;
        itemBuilder.skullOwner = skullOwner;
        itemBuilder.slot = slot;

        return itemBuilder;
    }
}
