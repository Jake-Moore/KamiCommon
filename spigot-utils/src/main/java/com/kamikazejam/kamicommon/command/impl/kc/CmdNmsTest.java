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
import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter;
import com.kamikazejam.kamicommon.nms.provider.event.PreSpawnSpawnerAdapter;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.provider.BlockUtilProvider;
import com.kamikazejam.kamicommon.nms.provider.ChatColorProvider;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.ClickAction;
import com.kamikazejam.kamicommon.nms.util.VersionedComponentUtil;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class CmdNmsTest extends KamiCommand implements Listener {
    private final List<Test> tests;
    /** Mirrors every test message into the server log; see {@link TranscribingSerializer}. */
    private final TranscribingSerializer serializer;

    public CmdNmsTest(@NotNull KamiPlugin plugin) {
        addAliases("nmstest");

        addRequirements(RequirementHasPerm.get("kamicommon.command.nmstest"));
        addRequirements(RequirementIsPlayer.get());

        serializer = new TranscribingSerializer(plugin.getLogger());
        tests = createTests(this, serializer);

        // Register as a Listener for debugging or event monitoring during tests
        plugin.registerListeners(this);
    }

    @NotNull
    private static TestResult runTest(Test test, Player player, TranscribingSerializer serializer) {
        try {
            int tickDelay = test.run(player);
            serializer.logger.info("[nmstest] PASS " + serializer.currentTest());
            return new TestResult(true, tickDelay, false);
        } catch (ExpectedUnsupported unsupported) {
            serializer.logger.info("[nmstest] N/A " + serializer.currentTest()
                    + ": " + unsupported.getMessage());
            serializer.fromMiniMessage(
                    "    <yellow>n/a on this version, by design: <white>" + unsupported.getMessage()
            ).sendTo(player);
            return new TestResult(false, 0, true);
        } catch (Throwable e) {
            serializer.logger.log(Level.SEVERE, "[nmstest] FAIL " + serializer.currentTest()
                    + ": " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            serializer.fromMiniMessage(
                    "    <red>FAILURE (see console): <white>" + e.getMessage()
            ).sendTo(player);
            return new TestResult(false, 0, false);
        }
    }

    /**
     * A capability this server version does not support, and whose API documents the refusal.
     * <p>
     * Reported apart from both a pass and a failure. Grading a documented refusal as a failure makes
     * the suite show red on every run of that version, which teaches its reader to ignore it;
     * grading it as a pass claims something was exercised that was not.
     * </p>
     */
    private static class ExpectedUnsupported extends RuntimeException {
        private ExpectedUnsupported(@NotNull String reason) {
            super(reason);
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Logic: Asynchronous/Delayed Execution
    // ------------------------------------------------------------------------------------------------

    private static @NotNull List<Test> createTests(@NotNull KamiCommand self,
                                                   @NotNull VersionedComponentSerializer serializer) {
        return Arrays.asList(
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
                    Block block = Bukkit.getWorlds().get(0).getBlockAt(0, 0, 0);
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
                    String namespaced = NmsAPI.getNamespaced(enchant);
                    if (namespaced.isEmpty()) {
                        throw new IllegalStateException("getNamespaced returned an empty string");
                    }
                    serializer.fromMiniMessage("    <gray>Success: " + namespaced).sendTo(player);
                    return 0;
                },

                // Entity Methods Test
                (player) -> {
                    final DecimalFormat df2 = new DecimalFormat("#.###");
                    serializer.fromMiniMessage("<gray>Testing EntityMethods...").sendTo(player);
                    AbstractEntityMethods methods = NmsAPI.getEntityMethods();
                    World world = Bukkit.getWorlds().get(0);
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
                            if (height <= 0 || width <= 0) {
                                throw new IllegalStateException(type.name() + " measured H: " + height
                                        + " W: " + width + ", both must be positive");
                            }
                            break;
                        }
                    }
                    serializer.fromMiniMessage("    <gray>Success").sendTo(player);
                    return 0;
                },

                // ItemEditor Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing ItemEditorProvider...").sendTo(player);
                    AbstractItemEditor editor = NmsAPI.getItemEditor();
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    if (!editor.isDamageable(sword)) {
                        throw new IllegalStateException("DIAMOND_SWORD reported as not damageable");
                    }
                    sword = editor.setDamage(sword, 42);
                    int damage = editor.getDamage(sword);
                    if (damage != 42) {
                        throw new IllegalStateException("setDamage(42) read back as " + damage);
                    }
                    ItemMeta meta = Preconditions.checkNotNull(sword.getItemMeta(), "ItemMeta was null");
                    meta = editor.setUnbreakable(meta, true);
                    if (!editor.isUnbreakable(meta)) {
                        throw new IllegalStateException("setUnbreakable(true) read back as false");
                    }
                    serializer.fromMiniMessage("    <gray>Success: damage=" + damage + ", unbreakable=true").sendTo(player);
                    return 0;
                },

                // NmsItem Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing NmsItemProvider...").sendTo(player);
                    NmsItemMethods items = NmsAPI.getNmsItemMethods();
                    String name = items.getI18NItemName(new ItemStack(Material.DIAMOND_SWORD));
                    if (name.isEmpty()) {
                        throw new IllegalStateException("getI18NItemName returned an empty string");
                    }
                    serializer.fromMiniMessage("    <gray>Success: DIAMOND_SWORD -> " + name).sendTo(player);
                    return 0;
                },

                // PacketHandler Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing PacketHandlerProvider...").sendTo(player);
                    NMSPacketHandler handler = NmsAPI.getPacketHandler();
                    // Destroy a client-side entity id that does not exist. The packet still has to
                    //  build and serialise correctly, which is what we are exercising here.
                    handler.sendPacket(player, handler.createDestroyPacket(Integer.MAX_VALUE - 1));
                    serializer.fromMiniMessage("    <gray>Success: destroy packet built and sent").sendTo(player);
                    return 0;
                },

                // CommandMapModifier Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing CommandMapModifierProvider...").sendTo(player);
                    CommandMapModifier modifier = NmsAPI.getCommandMapModifier();
                    Map<String, Command> known = modifier.getKnownCommands();
                    // Emptiness is read from size(), not from isEmpty(). The map is the server's
                    // own: from 1.20.6 Paper returns a view backed by its Brigadier dispatcher, and
                    // on Paper 1.21.4 that view's isEmpty() is inverted, answering true while
                    // size() reports hundreds of commands.
                    if (known.size() == 0) {
                        throw new IllegalStateException("knownCommands map is empty");
                    }
                    // The map holds root commands. This one is a child of KamiCommonCommand, and
                    // registration puts one entry in the map per alias of that root, so the root's
                    // aliases are what the map can be asked for.
                    KamiCommand root = self.getRoot();
                    List<String> aliases = root.getAliases();
                    if (aliases.isEmpty()) {
                        throw new IllegalStateException("the root command declares no aliases, so nothing was registered");
                    }
                    for (String alias : aliases) {
                        if (!isKnown(known, alias)) {
                            throw new IllegalStateException("root command alias '" + alias
                                    + "' is absent from the knownCommands map");
                        }
                    }
                    serializer.fromMiniMessage("    <gray>Success: " + known.size()
                            + " commands known, root " + aliases + " present").sendTo(player);
                    return 0;
                },

                // ClickAction Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing ClickAction.COPY_TO_CLIPBOARD...").sendTo(player);
                    VersionedComponentSerializer components = NmsAPI.getVersionedComponentSerializer();
                    // COPY_TO_CLIPBOARD is documented to be unavailable below 1.16, which is where
                    // Minecraft added it. Every other version must express it.
                    boolean documentedUnsupported =
                            NmsVersion.getFormattedNmsInteger() < NmsVersionParser.getFormattedNmsInteger("1.16");
                    String value = "kc-nmstest-copy";
                    String miniMessage;
                    try {
                        miniMessage = components.fromPlainText("copy")
                                .click(ClickAction.COPY_TO_CLIPBOARD, value)
                                .serializeMiniMessage();
                    } catch (UnsupportedOperationException refused) {
                        // Only the versions that document the refusal may take this branch, so a
                        // tier that started throwing where it should not still fails.
                        if (!documentedUnsupported) {
                            throw refused;
                        }
                        throw new ExpectedUnsupported("COPY_TO_CLIPBOARD needs 1.16 or newer");
                    }
                    if (documentedUnsupported) {
                        throw new IllegalStateException(
                                "COPY_TO_CLIPBOARD is documented as unavailable below 1.16 and did not refuse");
                    }
                    if (!miniMessage.contains(value)) {
                        throw new IllegalStateException("copy value absent from the component: " + miniMessage);
                    }
                    serializer.fromMiniMessage("    <gray>Success: copy_to_clipboard carries " + value).sendTo(player);
                    return 0;
                },

                // ComponentLoggerAdapter Provider Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing ComponentLoggerAdapterProvider...").sendTo(player);
                    ComponentLoggerAdapter logger = NmsAPI.getComponentLoggerAdapter();
                    Plugin plugin = JavaPlugin.getProvidingPlugin(CmdNmsTest.class);
                    logger.log(plugin, serializer.fromMiniMessage("<green>nmstest: ComponentLoggerAdapter reached the console"), Level.INFO);
                    serializer.fromMiniMessage("    <gray>Success: logged to console (check server log)").sendTo(player);
                    return 0;
                },

                // PreSpawnSpawnerAdapter Test
                (player) -> {
                    serializer.fromMiniMessage("<gray>Testing PreSpawnSpawnerAdapter...").sendTo(player);
                    Listener adapter = PreSpawnSpawnerAdapter.getSpawnerAdapter();
                    if (adapter == null) {
                        throw new IllegalStateException("getSpawnerAdapter returned null");
                    }
                    serializer.fromMiniMessage("    <gray>Success: " + adapter.getClass().getSimpleName()).sendTo(player);
                    return 0;
                }
        );
    }

    /**
     * Whether the command map holds a command under the given name.
     * <p>
     * Read by key and then by the commands themselves, because the map is the server's own: from
     * 1.20.6 Paper returns a view backed by its Brigadier dispatcher, whose answers do not always
     * match a plain map's.
     * </p>
     *
     * @param known the server's known commands
     * @param name  the name to look for
     * @return true when a command is registered under that name
     */
    private static boolean isKnown(@NotNull Map<String, Command> known, @NotNull String name) {
        if (known.containsKey(name)) {
            return true;
        }
        for (Command command : known.values()) {
            if (command.getName().equals(name) || command.getAliases().contains(name)) {
                return true;
            }
        }
        return false;
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
        serializer.fromMiniMessage(
                "<gray>NMS Version: <white>" + NmsVersion.getMCVersion() + " <gray>(<white>" + NmsVersion.getFormattedNmsInteger() + "<gray>)"
        ).sendTo(player);
        serializer.fromMiniMessage(
                "  <gray>WineSpigot?: <white>" + NmsVersion.isWineSpigot()
        ).sendTo(player);

        // Run Tests
        AtomicInteger successes = new AtomicInteger(0);
        AtomicInteger notApplicable = new AtomicInteger(0);

        // Start the recursive test chain
        runTestsRecursive(0, player, serializer, successes, notApplicable, (v) -> {
            // Counted against what this version can actually be asked to do, so a documented refusal
            // neither fails the suite nor inflates the pass count.
            int applicable = tests.size() - notApplicable.get();
            boolean passed = successes.get() >= applicable;
            String tail = notApplicable.get() > 0
                    ? " (" + notApplicable.get() + " n/a on this version)" : "";

            // A single line an operator can grep for, whichever way it went.
            serializer.logger.info("[nmstest] RESULT: " + (passed ? "PASSED" : "FAILED")
                    + " " + successes.get() + "/" + applicable
                    + " on " + NmsVersion.getMCVersion() + " (" + NmsVersion.getFormattedNmsInteger() + ")"
                    + tail);

            // Send Results
            if (passed) {
                serializer.fromMiniMessage(
                        "<green>ALL TESTS PASSED! (" + successes + "/" + applicable + ")" + tail
                ).sendTo(player);
            } else {
                serializer.fromMiniMessage(
                        "<red>TEST SUITE FAILED! (" + successes + "/" + applicable + ")" + tail + " <bold>See Console."
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
            TranscribingSerializer serializer,
            AtomicInteger successes,
            AtomicInteger notApplicable,
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
        } else if (result.notApplicable) {
            notApplicable.incrementAndGet();
        }

        // Recursive Step: Schedule next test
        Runnable nextStep = () -> runTestsRecursive(index + 1, player, serializer, successes, notApplicable, onComplete);

        if (result.delayTicks > 0) {
            // Find the plugin instance to schedule the task
            Plugin plugin = JavaPlugin.getProvidingPlugin(CmdNmsTest.class);
            Bukkit.getScheduler().runTaskLater(plugin, nextStep, result.delayTicks);
        } else {
            // Run immediately (recursion, but safe for small lists like this)
            nextStep.run();
        }
    }

    /**
     * Wraps the real serializer so that everything a test tells the player is also written to the
     * server log, and so each test names itself.
     * <p>
     * The results of {@code /kc nmstest} used to exist only in the running player's chat. Whoever
     * operates the server, who for the version test matrix is not the person holding the mouse,
     * could see that the command ran and nothing else, and one check ({@code ComponentLoggerAdapter})
     * asks the reader to go and look at the console it was not printing to.
     * </p>
     * <p>
     * The test name is taken from the {@code "Testing X..."} line every test already sends, rather
     * than from a list kept alongside {@link #createTests}. A parallel list is one reordering away
     * from labelling the wrong failure, and nothing would catch it.
     * </p>
     * <p>
     * Single-threaded by construction: {@code /kc nmstest} runs one test at a time on the main
     * thread, chained by delay, so {@link #currentTest} cannot interleave.
     * </p>
     */
    private static class TranscribingSerializer extends VersionedComponentSerializer {
        private static final Pattern TESTING = Pattern.compile("^Testing (.+?)\\.\\.\\.$");
        private static final Pattern TAGS = Pattern.compile("<[^>]+>");

        private final @NotNull Logger logger;
        private @Nullable String currentTest = null;

        private TranscribingSerializer(@NotNull Logger logger) {
            this.logger = logger;
        }

        @Override
        public @NotNull com.kamikazejam.kamicommon.nms.text.VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
            String plain = TAGS.matcher(miniMessage).replaceAll("").trim();
            Matcher matcher = TESTING.matcher(plain);
            if (matcher.matches()) {
                currentTest = matcher.group(1);
            }
            if (!plain.isEmpty()) {
                logger.info("[nmstest] " + plain);
            }
            return super.fromMiniMessage(miniMessage);
        }

        private @NotNull String currentTest() {
            return currentTest == null ? "<unnamed>" : currentTest;
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
        /** The capability is unsupported on this version by design, so it is neither a pass nor a failure. */
        public final boolean notApplicable;
    }
}
