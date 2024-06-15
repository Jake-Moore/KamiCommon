package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.Action;
import com.kamikazejam.kamicommon.nms.provider.BlockUtilProvider;
import com.kamikazejam.kamicommon.nms.provider.ChatColorProvider;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.Color;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class CmdNmsTest extends KamiCommand {
    public CmdNmsTest() {
        addAliases("nmstest");

        addRequirements(RequirementHasPerm.get("kamicommon.command.nmstest"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() {
        Player plr = (Player) sender;
        plr.sendMessage(StringUtil.t("&7NMS Version: &f" + NmsVersion.getMCVersion() + " &7(&f" + NmsVersion.getFormattedNmsInteger() + "&7)"));
        plr.sendMessage(StringUtil.t("  &7WineSpigot?: &f" + NmsVersion.isWineSpigot()));

        // Chat Color Provider Test
        boolean t1 = runTest((player) -> {
            player.sendMessage(StringUtil.t("&7Testing ChatColorProvider..."));
            ChatColorProvider ccProvider = NmsAPI.getChatColorProvider();
            Color jColor = ccProvider.get().getColor(ChatColor.AQUA);
            player.sendMessage(StringUtil.t("    &7Success"));
        }, plr);

        // Block Util Provider Test
        boolean t2 = runTest((player) -> {
            player.sendMessage(StringUtil.t("&7Testing BlockUtilProvider..."));
            BlockUtilProvider buProvider = NmsAPI.getBlockUtilProvider();
            Block block = Bukkit.getWorlds().getFirst().getBlockAt(0, 0, 0);
            Material oldType = block.getType();
            buProvider.get().setBlockSuperFast(block, XMaterial.IRON_BLOCK, PlaceType.BUKKIT);
            buProvider.get().setBlockSuperFast(block, XMaterial.DIAMOND_BLOCK, PlaceType.NO_PHYSICS);
            buProvider.get().setBlockSuperFast(block, XMaterial.EMERALD_BLOCK, PlaceType.NMS);
            block.setType(oldType);
            player.sendMessage(StringUtil.t("    &7Success"));
        }, plr);

        // MessageManager Test
        boolean t3 = runTest((player) -> {
            player.sendMessage(StringUtil.t("&7Testing MessageManager..."));
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(StringUtil.t("&c&lTest Item"));
            item.setItemMeta(meta);
            Action clickCmd = new Action("<1>", "&aClickCmd").setClickRunCommand("/help");
            Action clickSug = new Action("<2>", "&bClickSug").setClickSuggestCommand("help");
            Action clickUrl = new Action("<3>", "&cClickUrl").setClickOpenURL("https://google.com");
            Action hoverText = new Action("<4>", "&dHoverText").setHoverText(StringUtil.t("&bThis is hover text"));
            Action hoverItem = new Action("<5>", "&eHoverItem").setHoverItem(item);
            Action combined = new Action("<6>", "&fCombined").setClickSuggestCommand("help").setHoverText(StringUtil.t("&bThis is hover text"));
            String message = "Test: <1> <2> <3> <4> <5> <6>";
            NmsAPI.getMessageManager().processAndSend(player, message, clickCmd, clickSug, clickUrl, hoverText, hoverItem, combined);
        }, plr);

        // Teleport Provider Test
        boolean t4 = runTest((player) -> {
            player.sendMessage(StringUtil.t("&7Testing TeleportProvider..."));
            NmsAPI.getTeleporter().teleportWithoutEvent(player, player.getLocation().clone().add(0, 0.5, 0));
            player.sendMessage(StringUtil.t("    &7Success"));
        }, plr);

        // Main Hand Provider
        boolean t5 = runTest((player) -> {
            player.sendMessage(StringUtil.t("&7Testing MainHandProvider..."));
            ItemStack stack = NmsAPI.getItemInMainHand(player);
            player.sendMessage(StringUtil.t("    &7Success: " + (stack == null ? "AIR" : stack.getType().name())));
        }, plr);

        // Enchant ID Provider
        boolean t6 = runTest((player) -> {
            player.sendMessage(StringUtil.t("&7Testing EnchantIDProvider..."));
            player.sendMessage(StringUtil.t("    &7Success: " + NmsAPI.getNamespaced(Enchantment.DAMAGE_ALL)));
        }, plr);

        if (t1 && t2 && t3 && t4 && t5 && t6) {
            plr.sendMessage(StringUtil.t("&aALL TESTS PASSED!"));
        }else {
            plr.sendMessage(StringUtil.t("&cTEST SUITE FAILED! See Console."));
        }
    }


    // Test Interface
    public interface Test {
        void run(Player player);
    }
    private static boolean runTest(Test test, Player player) {
        try {
            test.run(player);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(StringUtil.t("    &cFAILURE (see console)"));
            return false;
        }
    }
}
