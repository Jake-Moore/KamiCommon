package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.config.testing.Config;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Config config = new Config(this, new File(getDataFolder(), "config.yml"));


//        ItemStack itemStack = new ItemBuilder(XMaterial.DIAMOND_SWORD)
//                .setDisplayName("TestItem")
//                .setLore("Test1", "Test2")
//                .addEnchant(XEnchantment.DAMAGE_ALL.getEnchant(), 1)
//                .addFlag(ItemFlag.HIDE_ENCHANTS)
//                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
//                .setNbtBoolean("FR-FREE-ITEM", true)
//                .setUnbreakable(true)
//                .build();
//
//        config.set("TestItem", itemStack);
//        config.save();
//
//        ItemStack itemStack1 = config.getItemStack("TestItem");
//        config.set("TestItemCopied", itemStack1);
        config.save();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("KamiCommon enabled");

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    HoverItem hover = new HoverItem("{item}", "ItemStack", itemStack1);
//                    MessageActionManager.processAndSend(player, "Test {item}", hover);
//
//                    ActionBar.sendActionBar(player, "Test Action Bar");
//                }
//            }
//        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("KamiCommon disabled");
    }
}
