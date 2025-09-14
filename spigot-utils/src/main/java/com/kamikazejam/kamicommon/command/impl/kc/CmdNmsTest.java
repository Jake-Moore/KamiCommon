package com.kamikazejam.kamicommon.command.impl.kc;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.actions.Action;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.provider.BlockUtilProvider;
import com.kamikazejam.kamicommon.nms.provider.ChatColorProvider;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.util.VersionedComponentUtil;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.Preconditions;
import nl.marido.deluxecombat.shaded.xseries.XEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class CmdNmsTest extends KamiCommand {
    private final List<Test> tests;
    public CmdNmsTest() {
        addAliases("nmstest");

        addRequirements(RequirementHasPerm.get("kamicommon.command.nmstest"));
        addRequirements(RequirementIsPlayer.get());

        tests = createTests(NmsAPI.getVersionedComponentSerializer());
    }

    @Override
    public void perform(@NotNull CommandContext context) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        Player player = (Player) context.getSender();
        serializer.fromMiniMessage(
                "<gray>NMS Version: <white>" + NmsVersion.getMCVersion() + " <gray>(<white>" + NmsVersion.getFormattedNmsInteger() + "<gray>)"
        ).sendTo(player);
        serializer.fromMiniMessage(
                "  <gray>WineSpigot?: <white>" + NmsVersion.isWineSpigot()
        ).sendTo(player);

        int successes = 0;
        for (Test test : tests) {
            if (runTest(test, player, serializer)) { successes++; }
        }

        // Send Results
        if (successes >= tests.size()) {
            serializer.fromMiniMessage(
                    "<green>ALL TESTS PASSED! (" + successes + "/" + tests.size() + ")"
            ).sendTo(player);
        }else {
            serializer.fromMiniMessage(
                    "<red>TEST SUITE FAILED! (" + successes + "/" + tests.size() + ") <bold>See Console."
            ).sendTo(player);
        }
    }

    private static @NotNull List<Test> createTests(@NotNull VersionedComponentSerializer serializer) {
        return List.of(
                // Chat Color Provider Test
                (player) -> {
                    serializer.fromMiniMessage(
                            "<gray>Testing ChatColorProvider..."
                    ).sendTo(player);
                    ChatColorProvider ccProvider = NmsAPI.getChatColorProvider();
                    @Nullable Color jColor = ccProvider.get().getColor(ChatColor.AQUA);
                    if (jColor == null) {
                        serializer.fromMiniMessage(
                                "    <red>Failure: ChatColor.AQUA maps to null!"
                        ).sendTo(player);
                    } else {
                        serializer.fromMiniMessage(
                                "    <gray>Success: ChatColor.AQUA mapped to RGB(" + jColor.getRed() + "," + jColor.getGreen() + "," + jColor.getBlue() + ")"
                        ).sendTo(player);
                    }
                },

                // Block Util Provider Test
                (player) -> {
                    serializer.fromMiniMessage(
                            "<gray>Testing BlockUtilProvider..."
                    ).sendTo(player);
                    BlockUtilProvider buProvider = NmsAPI.getBlockUtilProvider();
                    Block block = Bukkit.getWorlds().getFirst().getBlockAt(0, 0, 0);
                    Material oldType = block.getType();
                    buProvider.get().setBlockSuperFast(block, XMaterial.IRON_BLOCK, PlaceType.BUKKIT);
                    buProvider.get().setBlockSuperFast(block, XMaterial.DIAMOND_BLOCK, PlaceType.NO_PHYSICS);
                    buProvider.get().setBlockSuperFast(block, XMaterial.EMERALD_BLOCK, PlaceType.NMS);
                    block.setType(oldType);
                    serializer.fromMiniMessage(
                            "    <gray>Success"
                    ).sendTo(player);
                },

                // MessageManager Test
                (player) -> {
                    serializer.fromMiniMessage(
                            "<gray>Testing MessageManager..."
                    ).sendTo(player);
                    ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
                    ItemMeta meta = item.getItemMeta();
                    VersionedComponentUtil.setDisplayName(meta, serializer.fromMiniMessage("<red><bold>Test Item"));
                    item.setItemMeta(meta);
                    Action clickCmd = new Action("<1>", "&aClickCmd").setClickRunCommand("/help");
                    Action clickSug = new Action("<2>", "&bClickSug").setClickSuggestCommand("help");
                    Action clickUrl = new Action("<3>", "&cClickUrl").setClickOpenURL("https://google.com");
                    Action hoverText = new Action("<4>", "&dHoverText").setHoverText(LegacyColors.t("&bThis is hover text"));
                    Action hoverItem = new Action("<5>", "&eHoverItem").setHoverItem(item);
                    Action combined = new Action("<6>", "&fCombined").setClickSuggestCommand("help").setHoverText(LegacyColors.t("&bThis is hover text"));
                    String message = "Test: <1> <2> <3> <4> <5> <6>";
                    NmsAPI.getMessageManager().processAndSend(player, message, clickCmd, clickSug, clickUrl, hoverText, hoverItem, combined);
                },

                // Teleport Provider Test
                (player) -> {
                    serializer.fromMiniMessage(
                            "<gray>Testing TeleportProvider..."
                    ).sendTo(player);
                    NmsAPI.getTeleporter().teleportWithoutEvent(player, player.getLocation().clone().add(0, 0.5, 0));
                    serializer.fromMiniMessage(
                            "    <gray>Success"
                    ).sendTo(player);
                },

                // Main Hand Provider
                (player) -> {
                    serializer.fromMiniMessage(
                            "<gray>Testing MainHandProvider..."
                    ).sendTo(player);
                    ItemStack stack = NmsAPI.getItemInMainHand(player);
                    serializer.fromMiniMessage(
                            "    <gray>Success: " + (stack == null ? "AIR" : stack.getType().name())
                    ).sendTo(player);
                },

                // Enchant ID Provider
                (player) -> {
                    serializer.fromMiniMessage(
                            "<gray>Testing EnchantIDProvider..."
                    ).sendTo(player);
                    Enchantment enchant = Preconditions.checkNotNull(XEnchantment.DAMAGE_ALL.getEnchant(), "Enchantment not found");
                    serializer.fromMiniMessage(
                            "    <gray>Success: " + NmsAPI.getNamespaced(enchant)
                    ).sendTo(player);
                },

                // Entity Methods Test
                (player) -> {
                    final DecimalFormat df2 = new DecimalFormat("#.###");
                    serializer.fromMiniMessage(
                            "<gray>Testing EntityMethods..."
                    ).sendTo(player);
                    AbstractEntityMethods methods = NmsAPI.getEntityMethods();
                    World world = Bukkit.getWorlds().getFirst();
                    Location location = new Location(world, 0, 245, 0);
                    for (EntityType type : EntityType.values()) {
                        if (!type.isSpawnable() || !type.isAlive()) { continue; }

                        serializer.fromMiniMessage(
                                "    <gray>" + type.name() + ":"
                        ).sendTo(player);
                        Entity entity = world.spawnEntity(location, type);
                        final double height = methods.getEntityHeight(entity);
                        final double width = methods.getEntityWidth(entity);
                        serializer.fromMiniMessage(
                                "      <gray>H: " + df2.format(height) +" W: " + df2.format(width)
                        ).sendTo(player);
                        entity.remove();
                    }
                    serializer.fromMiniMessage(
                            "    <gray>Success (see console)"
                    ).sendTo(player);
                }
        );
    }

    // Test Interface
    public interface Test {
        void run(Player player);
    }
    private static boolean runTest(Test test, Player player, VersionedComponentSerializer serializer) {
        try {
            test.run(player);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            serializer.fromMiniMessage(
                "    <red>FAILURE (see console)"
            ).sendTo(player);
            return false;
        }
    }
}
