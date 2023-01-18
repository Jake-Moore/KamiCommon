package com.kamikazejamplugins.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue", "FieldCanBeLocal"})
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

    @Override
    public ItemStack getBasicItem(ConfigurationSection config) {
        short damage = (short) config.getInt("damage", 0);
        int amount = config.getInt("amount", 1);

        String mat = config.getString("material");
        CustomStack customStack = CustomStack.getInstance(mat);

        ItemStack item;
        if (customStack != null) {
            item = customStack.getItemStack();
        }else {
            item = new ItemStack(Material.valueOf(config.getString("material")), amount, damage);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) { return item; }

        meta.setDisplayName(StringUtil.t(config.getString("name")));
        meta.setLore(StringUtil.t(config.getStringList("lore")));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack getPlayerHead(ConfigurationSection config, @Nullable OfflinePlayer offlinePlayer) {
        ItemStack item = getBasicItem(config);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        SkullMeta skullMeta = (SkullMeta) meta;

        // Set the skull owner if it's not null
        if (offlinePlayer != null) {
            skullMeta.setOwner(offlinePlayer.getName());
        }

        skullMeta.setDisplayName(meta.getDisplayName());
        skullMeta.setLore(meta.getLore());
        item.setItemMeta(skullMeta);
        return item;
    }

    @Override
    public IBuilder clone() {
        return new IAItemBuilder(this.is);
    }
}
