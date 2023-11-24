package com.kamikazejam.kamicommon.item;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.MemorySection;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings({"unused"})
public class IAItemBuilder extends IBuilder {

    public IAItemBuilder(MemorySection section) {
        super(section);
    }
    public IAItemBuilder(MemorySection section, OfflinePlayer offlinePlayer) {
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
    public void loadBasicItem(MemorySection config) {
        this.damage = (short) config.getInt("damage", 0);
        this.amount = config.getInt("amount", 1);

        this.name = config.getString("name");
        this.lore = config.getStringList("lore");

        String mat = config.getString("material", config.getString("type", null));
        if (mat == null) { return; }
        
        CustomStack customStack = CustomStack.getInstance(mat);
        ItemStack item;
        if (customStack != null) {
            this.base = customStack.getItemStack();
        }else {
            this.material = XMaterial.matchXMaterial(mat).orElseThrow(() -> new IllegalArgumentException("Invalid material: " + config.getString("material", config.getString("type"))));
        }
    }

    @Override
    public void loadPlayerHead(MemorySection config, @Nullable OfflinePlayer offlinePlayer) {
        loadBasicItem(config);
        // Set the skull owner if it's not null
        if (offlinePlayer != null) {
            this.skullOwner = offlinePlayer.getName();
        }
    }

    @Override
    public IBuilder clone() {
        return loadClone(new IAItemBuilder(this.material, this.amount, this.damage));
    }
}
