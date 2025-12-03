package com.kamikazejam.kamicommon.command.impl.kc;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.KamiPlugin;
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
import lombok.AllArgsConstructor;
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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class CmdNmsTest extends KamiCommand implements Listener {
    private final List<Test> tests;

    public CmdNmsTest(@NotNull KamiPlugin plugin) {
        addAliases("nmstest");

        addRequirements(RequirementHasPerm.get("kamicommon.command.nmstest"));
        addRequirements(RequirementIsPlayer.get());

        tests = createTests(NmsAPI.getVersionedComponentSerializer());

        // Register as a Listener for debugging or event monitoring during tests
        plugin.registerListeners(this);
    }

    @NotNull
    private static TestResult runTest(Test test, Player player, VersionedComponentSerializer serializer) {
        try {
            int tickDelay = test.run(player);
            return new TestResult(true, tickDelay);
        } catch (Throwable e) {
            e.printStackTrace();
            serializer.fromMiniMessage(
                    "    <red>FAILURE (see console): <white>" + e.getMessage()
            ).sendTo(player);
            return new TestResult(false, 0);
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Logic: Asynchronous/Delayed Execution
    // ------------------------------------------------------------------------------------------------

    private static @NotNull List<Test> createTests(@NotNull VersionedComponentSerializer serializer) {
        return List.of(
                // Chat Color Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing ChatColorProvider...").sendTo(player);
                    ChatColorProvider ccProvider = NmsAPI.getChatColorProvider();
                    @Nullable Color jColor = ccProvider.get().getColor(ChatColor.AQUA);
                    if (jColor == null) {
                        serializer.fromMiniMessage("    <red>Failure: ChatColor.AQUA maps to null!").sendTo(player);
                        throw new IllegalStateException("Color null");
                    } else {
                        serializer.fromMiniMessage("    <gray>Success: ChatColor.AQUA mapped to RGB(" + jColor.getRed() + "," + jColor.getGreen() + "," + jColor.getBlue() + ")").sendTo(player);
                    }
                    return 0; // No delay needed
                },

                // Block Util Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing BlockUtilProvider...").sendTo(player);
                    BlockUtilProvider buProvider = NmsAPI.getBlockUtilProvider();
                    Block block = Bukkit.getWorlds().getFirst().getBlockAt(0, 0, 0);
                    Material oldType = block.getType();
                    buProvider.get().setBlockSuperFast(block, XMaterial.IRON_BLOCK, PlaceType.BUKKIT);
                    buProvider.get().setBlockSuperFast(block, XMaterial.DIAMOND_BLOCK, PlaceType.NO_PHYSICS);
                    buProvider.get().setBlockSuperFast(block, XMaterial.EMERALD_BLOCK, PlaceType.NMS);
                    block.setType(oldType);
                    serializer.fromMiniMessage("    <gray>Success").sendTo(player);
                    return 2; // Slight delay for visual update
                },

                // MessageManager Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing MessageManager...").sendTo(player);
                    ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        VersionedComponentUtil.setDisplayName(meta, serializer.fromMiniMessage("<red><bold>Test Item"));
                        item.setItemMeta(meta);
                    }
                    Action clickCmd = new Action("<1>", "&aClickCmd").setClickRunCommand("/help");
                    Action clickSug = new Action("<2>", "&bClickSug").setClickSuggestCommand("help");
                    Action clickUrl = new Action("<3>", "&cClickUrl").setClickOpenURL("https://google.com");
                    Action hoverText = new Action("<4>", "&dHoverText").setHoverText(LegacyColors.t("&bThis is hover text"));
                    Action hoverItem = new Action("<5>", "&eHoverItem").setHoverItem(item);
                    Action combined = new Action("<6>", "&fCombined").setClickSuggestCommand("help").setHoverText(LegacyColors.t("&bThis is hover text"));
                    String message = "Test: <1> <2> <3> <4> <5> <6>";
                    NmsAPI.getMessageManager().processAndSend(player, message, clickCmd, clickSug, clickUrl, hoverText, hoverItem, combined);
                    return 10; // Delay so user can see chat
                },

                // Teleport Provider Test (Same World)
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing TeleportProvider (same world)...").sendTo(player);

                    // Test teleporting 1 block up
                    Location upward = player.getLocation().clone().add(0, 1.0, 0);
                    NmsAPI.getTeleporter().teleportWithoutEvent(player, upward);

                    // Validate position (within 0.5 blocks)
                    Location after = player.getLocation();
                    double distance = after.distanceSquared(upward);
                    if (distance > 0.25) {
                        throw new IllegalStateException("Player not teleported to correct location! Distance squared: " + distance);
                    }
                    serializer.fromMiniMessage("    <gray>Success (waiting 1 second before next test)").sendTo(player);
                    return 1; // 1-Second delay to let chunks load/user to see they moved (same world doesn't take long)
                },

                // Teleport Provider Test (Different World)
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing TeleportProvider (different world)...").sendTo(player);

                    // Find a different world
                    @Nullable World targetWorld = getTargetWorld(player);
                    if (targetWorld == null) {
                        serializer.fromMiniMessage("    <yellow>Skipping: No other world found on server.").sendTo(player);
                        return 0;
                    }
                    serializer.fromMiniMessage("    <gray>Identified target world: <white>" + targetWorld.getName()).sendTo(player);

                    // Teleport to different world
                    Location targetLocation = new Location(targetWorld, 0, 150, 0);
                    NmsAPI.getTeleporter().teleportWithoutEvent(player, targetLocation);

                    // Validate position
                    Location after = player.getLocation();
                    // Basic world check + approximate distance (ignoring high precision due to load times)
                    if (!after.getWorld().getName().equals(targetWorld.getName())) {
                        throw new IllegalStateException("Player world not updated. Expected: " + targetWorld.getName() + " Got: " + after.getWorld().getName());
                    }

                    serializer.fromMiniMessage("    <gray>Success (waiting 3 seconds before next test)").sendTo(player);
                    return 60; // 3-Second delay to process download/rendering
                },

                // Main Hand Provider
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing MainHandProvider...").sendTo(player);
                    ItemStack stack = NmsAPI.getItemInMainHand(player);
                    serializer.fromMiniMessage("    <gray>Success: " + (stack == null ? "AIR" : stack.getType().name())).sendTo(player);
                    return 0;
                },

                // Enchant ID Provider
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing EnchantIDProvider...").sendTo(player);
                    Enchantment enchant = Preconditions.checkNotNull(XEnchantment.SHARPNESS.get(), "Enchantment not found");
                    serializer.fromMiniMessage("    <gray>Success: " + NmsAPI.getNamespaced(enchant)).sendTo(player);
                    return 0;
                },

                // Entity Methods Test
                (player) -> {
                    final DecimalFormat df2 = new DecimalFormat("#.###");
                    serializer.fromMiniMessage("<gray>Testing EntityMethods...").sendTo(player);
                    AbstractEntityMethods methods = NmsAPI.getEntityMethods();
                    World world = Bukkit.getWorlds().getFirst();
                    Location location = new Location(world, 0, 245, 0);
                    for (EntityType type : EntityType.values()) {
                        if (!type.isSpawnable() || !type.isAlive()) {
                            continue;
                        }

                        // Just test one entity to avoid spam/lag in test suite
                        if (type == EntityType.ZOMBIE) {
                            serializer.fromMiniMessage("    <gray>" + type.name() + ":").sendTo(player);
                            Entity entity = world.spawnEntity(location, type);
                            final double height = methods.getEntityHeight(entity);
                            final double width = methods.getEntityWidth(entity);
                            serializer.fromMiniMessage("      <gray>H: " + df2.format(height) + " W: " + df2.format(width)).sendTo(player);
                            entity.remove();
                            break;
                        }
                    }
                    serializer.fromMiniMessage("    <gray>Success").sendTo(player);
                    return 0;
                }
        );
    }

    private static @Nullable World getTargetWorld(Player player) {
        World myWorld = player.getWorld();

        // 1. Find an ideal world (normal environment)
        @Nullable World idealWorld = null;
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equals(myWorld.getName())) continue;
            if (world.getEnvironment() == World.Environment.NORMAL) {
                idealWorld = world;
                break;
            }
        }
        if (idealWorld != null) return idealWorld;

        // 2. Fallback: Find any different world
        @Nullable World fallbackWorld = null;
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equals(myWorld.getName())) continue;
            fallbackWorld = world;
            break;
        }
        return fallbackWorld;
    }

    // ------------------------------------------------------------------------------------------------
    // Test Definitions
    // ------------------------------------------------------------------------------------------------

    @Override
    public void perform(@NotNull CommandContext context) {
        // Fetch Pre-Test Player Info
        Player player = (Player) context.getSender();
        Location origin = player.getLocation();

        // Send NMS Version Info
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        serializer.fromMiniMessage(
                "<gray>NMS Version: <white>" + NmsVersion.getMCVersion() + " <gray>(<white>" + NmsVersion.getFormattedNmsInteger() + "<gray>)"
        ).sendTo(player);
        serializer.fromMiniMessage(
                "  <gray>WineSpigot?: <white>" + NmsVersion.isWineSpigot()
        ).sendTo(player);

        // Run Tests
        AtomicInteger successes = new AtomicInteger(0);

        // Start the recursive test chain
        runTestsRecursive(0, player, serializer, successes, (v) -> {
            // Send Results
            if (successes.get() >= tests.size()) {
                serializer.fromMiniMessage(
                        "<green>ALL TESTS PASSED! (" + successes + "/" + tests.size() + ")"
                ).sendTo(player);
            } else {
                serializer.fromMiniMessage(
                        "<red>TEST SUITE FAILED! (" + successes + "/" + tests.size() + ") <bold>See Console."
                ).sendTo(player);
            }

            // Return Player to Origin
            //   Clear momentum and fall tracking before teleport
            player.setVelocity(new Vector(0, 0, 0)); // zero velocity
            player.setFallDistance(0f); // reset tracked fall distance
            //   very short no-damage window to cover edge cases
            int oldNoDamageTicks = player.getNoDamageTicks();
            player.setNoDamageTicks(20); // 1 second immunity
            //   Teleport back using bukkit API in case NMS teleporter has issues
            player.teleport(origin);
        });
    }

    private void runTestsRecursive(
            int index,
            Player player,
            VersionedComponentSerializer serializer,
            AtomicInteger successes,
            Consumer<Void> onComplete
    ) {
        // Base Case: All tests finished
        if (index >= tests.size()) {
            onComplete.accept(null);
            return;
        }

        // Run the current test
        Test test = tests.get(index);
        TestResult result = runTest(test, player, serializer);

        if (result.success) {
            successes.incrementAndGet();
        }

        // Recursive Step: Schedule next test
        Runnable nextStep = () -> runTestsRecursive(index + 1, player, serializer, successes, onComplete);

        if (result.delayTicks > 0) {
            // Find the plugin instance to schedule the task
            Plugin plugin = JavaPlugin.getProvidingPlugin(CmdNmsTest.class);
            Bukkit.getScheduler().runTaskLater(plugin, nextStep, result.delayTicks);
        } else {
            // Run immediately (recursion, but safe for small lists like this)
            nextStep.run();
        }
    }

    // Test Interface
    public interface Test {
        /**
         * @return the number of ticks to delay after the test completes (before the next test starts)
         */
        int run(Player player);
    }

    @AllArgsConstructor
    public static class TestResult {
        public final boolean success;
        public final int delayTicks;
    }
}
