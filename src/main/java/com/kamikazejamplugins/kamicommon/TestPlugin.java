package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.config.testing.Config;
import com.kamikazejamplugins.kamicommon.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Config config = new Config(this, new File(getDataFolder(), "config.yml"));

        ItemStack itemStack = new ItemBuilder(Material.DIAMOND_SWORD)
                .setDisplayName("TestItem")
                .setLore("Test1", "Test2")
                .addEnchant(Enchantment.DAMAGE_ALL, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .setNbtBoolean("FR-FREE-ITEM", true)
                .setUnbreakable(true)
                .build();

        config.set("TestItem", itemStack);
        config.save();

        ItemStack itemStack1 = config.getItemStack("TestItem");
        config.set("TestItemCopied", itemStack1);
        config.save();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("KamiCommon enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("KamiCommon disabled");
    }
}
