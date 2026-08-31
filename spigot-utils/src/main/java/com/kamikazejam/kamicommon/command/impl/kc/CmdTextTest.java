package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.actions.Action;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.ClickAction;
import com.kamikazejam.kamicommon.nms.text.TextDecoration;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.util.VersionedComponentUtil;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Asserts on what {@link VersionedComponent} emits, across the whole of its surface.
 * <p>
 * Every other verification in this library checks packaging: which classes are present, what
 * bytecode level they carry, which provider a version ladder selects. None of them reads a
 * serializer's output, and defects reached releases because of that, {@code click()} for nine of
 * them. Each case here builds a component, takes the form this server's tier sends, and asserts on
 * the structural shape the receiving end reads rather than on payload text appearing somewhere.
 * </p>
 * <p>
 * Console mode is the reason this exists. Verification that needs a person logged in does not get
 * run across a version matrix, so the assertions live where a script can drive them: one
 * {@code PASS} or {@code FAIL} line per case and a {@code RESULT} line carrying counts, with
 * failures logged at {@link Level#SEVERE}. Player mode sends the same components to a client, for
 * the one question a wire assertion cannot answer, which is whether the client draws them.
 * </p>
 * <p>
 * The {@code msg.} cases cover {@link AbstractMessageManager}, which is a second and entirely
 * separate click and hover implementation reached only through
 * {@link AbstractMessageManager#processAndSend}. It shares no code with {@link VersionedComponent},
 * so a defect in one is invisible to every case that exercises the other.
 * </p>
 */
@SuppressWarnings("SpellCheckingInspection")
public class CmdTextTest extends KamiCommand {

    private static final String PREFIX = "[texttest] ";

    // Markers rather than prose. Each is unique, free of spaces so that structural matching may
    // ignore whitespace, and free of anything JSON or NBT would escape.
    private static final String ITEM_NAME = "KCTEXTTESTNAME";
    private static final String LORE_1 = "KCTEXTTESTLOREONE";
    private static final String LORE_2 = "KCTEXTTESTLORETWO";
    private static final String LORE_3 = "KCTEXTTESTLORETHREE";
    private static final String RUN_VALUE = "/kctexttest-run";
    private static final String SUGGEST_VALUE = "/kctexttest-suggest";
    private static final String URL_VALUE = "https://luxiouslabs.net/kctexttest";
    private static final String COPY_VALUE = "kctexttest-copy";
    private static final String HEX = "#ff00aa";
    /** The id of the item {@code testItem} builds, which the wire must carry exactly once. */
    private static final String ITEM_ID = "minecraft:diamond_sword";
    private static final String MENU_TITLE = "KCTEXTTESTMENU";

    // The MessageManager markers. The line has text on both sides of the placeholder because the
    // bungee implementation splits the line on the placeholder and indexes the resulting parts, so a
    // line that is nothing but the placeholder is a different code path from the one plugins use.
    private static final String MSG_HEAD = "KCMSGHEAD";
    private static final String MSG_TAIL = "KCMSGTAIL";
    private static final String MSG_PLACEHOLDER = "{kcmsg}";
    private static final String MSG_LINE = MSG_HEAD + " " + MSG_PLACEHOLDER + " " + MSG_TAIL;
    private static final String MSG_LABEL = "KCMSGLABEL";
    private static final String MSG_TIP = "KCMSGTIP";
    /** Already carries the slash that {@code setClickRunCommand} would otherwise add. */
    private static final String MSG_RUN = "/kcmsgtest-run";
    private static final String MSG_SUGGEST = "/kcmsgtest-suggest";
    private static final String MSG_URL = "https://luxiouslabs.net/kcmsgtest";
    /**
     * The first version whose {@code Player} can be proxied.
     * <p>
     * Up to 1.8.9 {@code org.bukkit.entity.Damageable} declares {@code getHealth()} twice, once
     * returning {@code int} and once returning {@code double}, which {@link Proxy} rejects outright
     * and which no Java class can implement either. The bungee side of
     * {@link AbstractMessageManager} builds components only for a {@link Player}, so on those
     * versions the component path cannot be reached from the console at all.
     * </p>
     */
    private static final String PLAYER_PROXY_FLOOR = "1.9";

    /** Never queried by Bukkit, and required because createInventory takes a non-null holder. */
    private static final InventoryHolder HOLDER = new InventoryHolder() {
        @Override
        public Inventory getInventory() {
            return null;
        }
    };

    public CmdTextTest() {
        addAliases("texttest");
        addRequirements(RequirementHasPerm.get("kamicommon.command.texttest"));
    }

    @Override
    public void perform(@NotNull CommandContext context) {
        Logger logger = Bukkit.getLogger();
        VersionedComponentSerializer ser = NmsAPI.getVersionedComponentSerializer();

        String tier = tierName(ser);
        @Nullable Profile profile = Profile.forTier(tier);
        logger.info(PREFIX + "START " + NmsVersion.getMCVersion() + " ("
                + NmsVersion.getFormattedNmsInteger() + ") tier=" + tier + " transport="
                + (profile == null ? "UNKNOWN" : profile.transport) + " java="
                + System.getProperty("java.specification.version"));

        Results results = new Results(logger);
        if (profile == null) {
            // Not a skip. A tier with no stated expectations is a tier nothing is checking, which is
            // the condition this command exists to remove.
            results.fail("tier.known", "no expectations are declared for " + tier
                    + "; add a Profile entry rather than leaving this version unchecked");
        } else {
            results.pass("tier.known");
            for (Map.Entry<String, Case> entry : cases(ser, profile, logger).entrySet()) {
                run(results, entry.getKey(), entry.getValue());
            }
        }

        String summary = PREFIX + "RESULT: " + (results.ok() ? "PASSED" : "FAILED") + " "
                + results.passed + "/" + results.total() + " on " + NmsVersion.getMCVersion()
                + " (" + NmsVersion.getFormattedNmsInteger() + ") tier=" + tier
                + " transport=" + (profile == null ? "UNKNOWN" : profile.transport)
                + (results.ok() ? "" : "; failed: " + String.join(", ", results.failed));
        logger.log(results.ok() ? Level.INFO : Level.SEVERE, summary);

        @Nullable Player me = context.getMe();
        if (me != null) {
            showTo(me, ser, profile);
        }
        ser.fromMiniMessage((results.ok() ? "<green>" : "<red>") + "texttest: " + results.passed
                + "/" + results.total() + " on " + tier
                + ". See console for the line of any case that failed."
        ).sendTo(context.getSender());
    }

    // ----------------------------------------------------------------------------------------- //
    // Cases
    // ----------------------------------------------------------------------------------------- //

    /**
     * The cases, in the order they are reported.
     *
     * @param ser     the serializer this server dispatches through
     * @param profile what this server's tier is expected to emit
     * @param logger  where the italic cases write what they measured
     * @return case name to case
     */
    private static @NotNull Map<String, Case> cases(final @NotNull VersionedComponentSerializer ser,
                                                    final @NotNull Profile profile,
                                                    final @NotNull Logger logger) {
        Map<String, Case> map = new LinkedHashMap<String, Case>();

        // Where a component goes when it is sent, and whether the form every case below reads is the
        // form that was sent. Without this the rest of the file could be asserting on a string no
        // client ever receives, which is the whole failure being guarded against.
        map.put("wire.sendPath", () -> sendPath(ser, profile));

        // The five factories. Each is judged on the text it carries and on the one attribute the
        // input asked for arriving on the wire.
        map.put("from.plainText", () -> {
            VersionedComponent c = ser.fromPlainText("PLAINA");
            return expect(c, profile).text("PLAINA").has("PLAINA", "text absent from the wire").result();
        });
        map.put("from.miniMessage", () -> {
            VersionedComponent c = ser.fromMiniMessage("<red>MINIA");
            return expect(c, profile).text("MINIA").has("\"color\":\"red\"", "colour lost").result();
        });
        map.put("from.legacyAmpersand", () -> {
            VersionedComponent c = ser.fromLegacyAmpersand("&aLEGAMPA");
            return expect(c, profile).text("LEGAMPA").has("\"color\":\"green\"", "colour lost").result();
        });
        map.put("from.legacySection", () -> {
            VersionedComponent c = ser.fromLegacySection("§9LEGSECA");
            return expect(c, profile).text("LEGSECA").has("\"color\":\"blue\"", "colour lost").result();
        });
        map.put("from.json", () -> {
            VersionedComponent c = ser.fromJson("{\"text\":\"JSONA\",\"color\":\"gold\"}");
            return expect(c, profile).text("JSONA").has("\"color\":\"gold\"", "colour lost").result();
        });

        // The five serialize methods, on one component whose expected output is the same on every
        // tier. These are the forms consumers read back, compared exactly where the form is exact and
        // structurally where member order is not defined.
        map.put("serialize.miniMessage", () -> {
            String s = ser.fromMiniMessage("<red>Hello").serializeMiniMessage();
            return s.contains("<red>") && s.contains("Hello") ? null
                    : "expected <red> and Hello, got <" + s + ">";
        });
        map.put("serialize.plainText", () ->
                sameText("Hello", ser.fromMiniMessage("<red>Hello").serializePlainText()));
        map.put("serialize.legacyAmpersand", () ->
                sameText("&cHello", ser.fromMiniMessage("<red>Hello").serializeLegacyAmpersand()));
        map.put("serialize.legacySection", () ->
                sameText("§cHello", ser.fromMiniMessage("<red>Hello").serializeLegacySection()));
        map.put("serialize.json", () -> {
            String json = flat(ser.fromMiniMessage("<red>Hello").serializeJson());
            if (!json.contains("\"text\":\"Hello\"")) { return "no text member in " + json; }
            if (!json.contains("\"color\":\"red\"")) { return "no colour member in " + json; }
            return null;
        });

        // hover(VersionedComponent). Asserted on the event key the receiver reads, then the action,
        // then the payload. Payload alone is not evidence: the double-nesting defect produced a
        // string containing every payload it was given and rendered none of it.
        map.put("hover.text", () -> {
            VersionedComponent c = ser.fromPlainText("HOVERANCHOR").hover(ser.fromPlainText("HOVERTIP"));
            return expect(c, profile)
                    .hasEvent(profile.bungee(), "hoverEvent", "hover_event")
                    .has("\"action\":\"show_text\"", "no show_text action")
                    .has("HOVERTIP", "tooltip text absent")
                    .result();
        });

        // click(...), all four actions. The fourth cannot be expressed below 1.16 and must say so.
        map.put("click.runCommand", () -> click(ser, profile, ClickAction.RUN_COMMAND, "run_command", RUN_VALUE));
        map.put("click.suggestCommand", () -> click(ser, profile, ClickAction.SUGGEST_COMMAND, "suggest_command", SUGGEST_VALUE));
        map.put("click.openUrl", () -> click(ser, profile, ClickAction.OPEN_URL, "open_url", URL_VALUE));
        map.put("click.copyToClipboard", () -> {
            if (profile.copyToClipboard) {
                return click(ser, profile, ClickAction.COPY_TO_CLIPBOARD, "copy_to_clipboard", COPY_VALUE);
            }
            return throwsNaming(profile, UnsupportedOperationException.class,
                    () -> ser.fromPlainText("CLICKANCHOR").click(ClickAction.COPY_TO_CLIPBOARD, COPY_VALUE));
        });

        // hoverItem(ItemStack), on an item with a custom name, two lore lines and a non-zero Damage.
        // That combination is what makes the nesting check meaningful: the whole item NBT and the tag
        // compound differ only in the members that sit outside the tag.
        map.put("hoverItem", () -> {
            ItemStack item = testItem(ser);
            if (!profile.hoverItem) {
                return throwsNaming(profile, UnsupportedOperationException.class,
                        () -> ser.fromPlainText("ITEMANCHOR").hoverItem(item));
            }
            VersionedComponent c = ser.fromPlainText("ITEMANCHOR").hoverItem(item);
            return expect(c, profile)
                    .hasEvent(profile.bungee(), "hoverEvent", "hover_event")
                    .has("show_item", "no show_item action")
                    .has(ITEM_NAME, "display name absent")
                    .has(LORE_1, "first lore line absent")
                    .has(LORE_2, "second lore line absent")
                    // The whole item NBT passed where the tag belongs. It renders as the bare item
                    // with no name and no lore, and every payload above is still present, so the
                    // claim has to be structural. Nesting the item inside its own tag writes the id
                    // a second time, which is true whatever order that version's NBT writer uses.
                    .hasOnce(ITEM_ID, "item NBT nested under a second tag key")
                    .result();
        });

        // All five decorations, plus the one call item rendering depends on.
        map.put("decorate.bold", () -> decoration(ser, profile, TextDecoration.BOLD, "bold", true));
        map.put("decorate.italic", () -> decoration(ser, profile, TextDecoration.ITALIC, "italic", true));
        map.put("decorate.underlined", () -> decoration(ser, profile, TextDecoration.UNDERLINED, "underlined", true));
        map.put("decorate.strikethrough", () -> decoration(ser, profile, TextDecoration.STRIKETHROUGH, "strikethrough", true));
        map.put("decorate.obfuscated", () -> decoration(ser, profile, TextDecoration.OBFUSCATED, "obfuscated", true));
        map.put("decorate.italicOff", () -> decoration(ser, profile, TextDecoration.ITALIC, "italic", false));

        map.put("append", () -> {
            VersionedComponent c = ser.fromPlainText("APPA").append(ser.fromPlainText("APPB"));
            return expect(c, profile).text("APPAAPPB")
                    .has("APPA", "left side absent from the wire")
                    .has("APPB", "right side absent from the wire")
                    .result();
        });

        // The ItemMeta round trip, written and read back through the library. The tiers do not agree
        // on the stored string form, and the claim that holds on all of them is that what went in
        // comes back out.
        map.put("itemMeta.displayName", () -> {
            ItemMeta meta = meta(new ItemStack(Material.DIAMOND_SWORD));
            VersionedComponentUtil.setDisplayName(meta, ser.fromMiniMessage("<gold>" + ITEM_NAME));
            @Nullable VersionedComponent read = VersionedComponentUtil.getDisplayName(meta);
            if (read == null) { return "getDisplayName returned null after setDisplayName"; }
            return sameText(ITEM_NAME, read.serializePlainText());
        });
        map.put("itemMeta.lore", () -> {
            ItemMeta meta = meta(new ItemStack(Material.DIAMOND_SWORD));
            VersionedComponentUtil.setLore(meta, Arrays.asList(
                    ser.fromMiniMessage("<gray>" + LORE_1), ser.fromMiniMessage("<gray>" + LORE_2)));
            @Nullable List<VersionedComponent> read = VersionedComponentUtil.getLore(meta);
            if (read == null) { return "getLore returned null after setLore"; }
            if (read.size() != 2) { return "expected 2 lore lines, got " + read.size(); }
            @Nullable String first = sameText(LORE_1, read.get(0).serializePlainText());
            return first != null ? first : sameText(LORE_2, read.get(1).serializePlainText());
        });
        map.put("itemMeta.addLoreLine", () -> {
            ItemMeta meta = meta(new ItemStack(Material.DIAMOND_SWORD));
            VersionedComponentUtil.setLore(meta, Arrays.asList(
                    ser.fromMiniMessage("<gray>" + LORE_1), ser.fromMiniMessage("<gray>" + LORE_2)));
            VersionedComponentUtil.addLoreLine(meta, ser.fromMiniMessage("<gray>" + LORE_3));
            @Nullable List<VersionedComponent> read = VersionedComponentUtil.getLore(meta);
            if (read == null) { return "getLore returned null after addLoreLine"; }
            if (read.size() != 3) { return "expected 3 lore lines, got " + read.size(); }
            return sameText(LORE_3, read.get(2).serializePlainText());
        });

        // Italics. Minecraft italicises a custom name and every lore line, so a component handed to
        // the server natively renders italic where the same text written as section-coded text does
        // not, and the same API call produced two different results either side of that boundary.
        // Asserted on the state read back out of the meta, and the serialized item is logged beside
        // it, because a pass line cannot show a rendering attribute.
        map.put("itemMeta.italicName", () -> {
            if (profile.hoverItem) {
                logger.info(PREFIX + "ITALIC item=" + readable(clip(flat(
                        wireFor(ser.fromPlainText("ITEMANCHOR").hoverItem(testItem(ser)), profile)))));
            }
            ItemMeta meta = meta(new ItemStack(Material.DIAMOND_SWORD));
            VersionedComponentUtil.setDisplayName(meta, ser.fromMiniMessage("<gold>" + ITEM_NAME));
            @Nullable VersionedComponent read = VersionedComponentUtil.getDisplayName(meta);
            if (read == null) { return "getDisplayName returned null after setDisplayName"; }
            evidence(logger, "name", profile.nativeItemName, read);
            return upright(profile.nativeItemName, read, "the display name");
        });
        map.put("itemMeta.italicLore", () -> {
            ItemMeta meta = meta(new ItemStack(Material.DIAMOND_SWORD));
            VersionedComponentUtil.setLore(meta, Arrays.asList(
                    ser.fromMiniMessage("<gray>" + LORE_1), ser.fromMiniMessage("<gray>" + LORE_2)));
            VersionedComponentUtil.addLoreLine(meta, ser.fromMiniMessage("<gray>" + LORE_3));
            @Nullable List<VersionedComponent> read = VersionedComponentUtil.getLore(meta);
            if (read == null) { return "getLore returned null after setLore"; }
            if (read.size() != 3) { return "expected 3 lore lines, got " + read.size(); }
            for (int line = 0; line < read.size(); line++) {
                evidence(logger, "lore" + (line + 1), profile.nativeItemLore, read.get(line));
                @Nullable String problem = upright(profile.nativeItemLore, read.get(line),
                        "lore line " + (line + 1));
                if (problem != null) { return problem; }
            }
            return null;
        });
        // Suppression, not an override. A caller that asked for italic keeps it, on both the name
        // and the lore, or the fix above has replaced one wrong answer with another.
        map.put("itemMeta.italicExplicit", () -> {
            ItemMeta meta = meta(new ItemStack(Material.DIAMOND_SWORD));
            VersionedComponentUtil.setDisplayName(meta,
                    ser.fromMiniMessage("<gold>" + ITEM_NAME).decorate(TextDecoration.ITALIC, true));
            VersionedComponentUtil.setLore(meta, Arrays.asList(
                    ser.fromMiniMessage("<gray>" + LORE_1).decorate(TextDecoration.ITALIC, true)));
            @Nullable VersionedComponent name = VersionedComponentUtil.getDisplayName(meta);
            if (name == null) { return "getDisplayName returned null after setDisplayName"; }
            evidence(logger, "name.explicit", profile.nativeItemName, name);
            @Nullable String problem = stillItalic(profile.nativeItemName, name, "the display name");
            if (problem != null) { return problem; }
            @Nullable List<VersionedComponent> lore = VersionedComponentUtil.getLore(meta);
            if (lore == null) { return "getLore returned null after setLore"; }
            if (lore.size() != 1) { return "expected 1 lore line, got " + lore.size(); }
            evidence(logger, "lore.explicit", profile.nativeItemLore, lore.get(0));
            return stillItalic(profile.nativeItemLore, lore.get(0), "the lore line");
        });

        // createInventory. The title is only readable through the API up to 1.13, so where it can be
        // read it is asserted, and where it cannot the size and type still are.
        map.put("createInventory.size", () -> {
            Inventory inv = ser.fromMiniMessage("<green>" + MENU_TITLE).createInventory(HOLDER, 9);
            if (inv.getSize() != 9) { return "expected size 9, got " + inv.getSize(); }
            return titleCarries(inv);
        });
        map.put("createInventory.type", () -> {
            Inventory inv = ser.fromMiniMessage("<green>" + MENU_TITLE).createInventory(HOLDER, InventoryType.HOPPER);
            if (inv.getType() != InventoryType.HOPPER) { return "expected HOPPER, got " + inv.getType(); }
            return titleCarries(inv);
        });

        // Hex either side of 1.16. Below it the wire must carry a named colour instead, because a
        // client that predates RGB reads nothing else and renders what it cannot read as white.
        map.put("hex", () -> {
            VersionedComponent c = ser.fromMiniMessage("<" + HEX + ">HEXA");
            Expect expect = expect(c, profile).text("HEXA");
            if (profile.hexOnWire) {
                return expect.hasHex("hex colour lost on a version that renders it").result();
            }
            return expect
                    .hasNoHex("hex colour reached a client that predates RGB")
                    .has("\"color\":\"", "no named colour to downsample onto")
                    .result();
        });

        addMessageCases(map, ser);

        return map;
    }

    private static @Nullable String click(@NotNull VersionedComponentSerializer ser, @NotNull Profile profile,
                                          @NotNull ClickAction action, @NotNull String wireAction,
                                          @NotNull String value) throws Exception {
        VersionedComponent c = ser.fromPlainText("CLICKANCHOR").click(action, value);
        return expect(c, profile)
                .hasEvent(profile.bungee(), "clickEvent", "click_event")
                .has("\"action\":\"" + wireAction + "\"", "no " + wireAction + " action")
                .has(value, "click value absent")
                .result();
    }

    private static @Nullable String decoration(@NotNull VersionedComponentSerializer ser, @NotNull Profile profile,
                                               @NotNull TextDecoration decoration,
                                               @NotNull String wireKey, boolean value) throws Exception {
        VersionedComponent c = ser.fromPlainText("DECORATED").decorate(decoration, value);
        return expect(c, profile)
                .text("DECORATED")
                .has("\"" + wireKey + "\":" + value, wireKey + " was not set to " + value + " on the wire")
                .result();
    }

    /**
     * A throw is a contract too. It must be the declared type and must name the tier that refused,
     * because an exception that does not say where it came from cannot be acted on.
     */
    private static @Nullable String throwsNaming(@NotNull Profile profile, @NotNull Class<? extends Throwable> type,
                                                 @NotNull Body body) {
        try {
            body.run();
            return "expected " + type.getSimpleName() + " on " + profile.tier + ", nothing was thrown";
        } catch (Throwable thrown) {
            if (!type.isInstance(thrown)) {
                return "expected " + type.getSimpleName() + ", got " + thrown.getClass().getName()
                        + ": " + thrown.getMessage();
            }
            String message = thrown.getMessage() == null ? "" : thrown.getMessage();
            if (!message.contains(profile.tier)) {
                return "throw does not name " + profile.tier + ": " + message;
            }
            return null;
        }
    }

    /**
     * What one component read back out of item meta carries, in both forms a tier can store.
     * <p>
     * Written whether the case passes or fails. The attribute being fixed is invisible in a pass
     * line, and the two sides of the boundary can only be compared by reading both.
     * </p>
     */
    private static void evidence(@NotNull Logger logger, @NotNull String what, boolean nativeHandover,
                                 @NotNull VersionedComponent read) {
        logger.info(PREFIX + "ITALIC " + what + " native=" + nativeHandover
                + " json=" + flat(read.serializeJson())
                + " legacy=<" + readable(read.serializeLegacySection()) + ">");
    }

    /**
     * That a component read back out of item meta renders upright.
     * <p>
     * Where the tier hands the component over natively, the component itself has to state italic
     * false, because Minecraft italicises anything that leaves it unstated. Where the tier writes
     * section-coded text, the server states it during its own conversion, and what this asserts is
     * that the library added no italic of its own.
     * </p>
     */
    private static @Nullable String upright(boolean nativeHandover, @NotNull VersionedComponent read,
                                            @NotNull String what) {
        String json = flat(read.serializeJson());
        if (nativeHandover) {
            return json.contains("\"italic\":false") ? null
                    : "italic is not stated false on " + what + ", handed over natively: " + clip(json);
        }
        String legacy = read.serializeLegacySection();
        if (legacy.contains("\u00a7o")) {
            return "the section-coded form of " + what + " carries an italic code: <"
                    + readable(legacy) + ">";
        }
        return json.contains("\"italic\":true")
                ? "italic is stated true on " + what + ": " + clip(json) : null;
    }

    /** That an explicitly italic component is still italic after the round trip. */
    private static @Nullable String stillItalic(boolean nativeHandover, @NotNull VersionedComponent read,
                                                @NotNull String what) {
        if (nativeHandover) {
            String json = flat(read.serializeJson());
            return json.contains("\"italic\":true") ? null
                    : "explicit italic was lost from " + what + ": " + clip(json);
        }
        String legacy = read.serializeLegacySection();
        return legacy.contains("\u00a7o") ? null
                : "explicit italic was lost from " + what + ": <" + readable(legacy) + ">";
    }

    /** A sword with a custom name, two lore lines and a non-zero Damage. */
    private static @NotNull ItemStack testItem(@NotNull VersionedComponentSerializer ser) {
        ItemStack item = NmsAPI.getItemEditor().setDamage(new ItemStack(Material.DIAMOND_SWORD), 42);
        ItemMeta meta = meta(item);
        VersionedComponentUtil.setDisplayName(meta, ser.fromMiniMessage("<gold>" + ITEM_NAME));
        VersionedComponentUtil.setLore(meta, Arrays.asList(
                ser.fromMiniMessage("<gray>" + LORE_1), ser.fromMiniMessage("<gray>" + LORE_2)));
        item.setItemMeta(meta);
        return item;
    }

    private static @NotNull ItemMeta meta(@NotNull ItemStack item) {
        @Nullable ItemMeta meta = item.getItemMeta();
        if (meta == null) { throw new IllegalStateException("no ItemMeta for " + item.getType()); }
        return meta;
    }

    /** The title, where the running version still exposes it on {@link Inventory}. */
    private static @Nullable String titleCarries(@NotNull Inventory inventory) {
        String title;
        try {
            title = (String) Inventory.class.getMethod("getTitle").invoke(inventory);
        } catch (Throwable removed) {
            // Removed from Inventory in 1.14, where it moved to InventoryView and needs an open view.
            return null;
        }
        return title != null && title.contains(MENU_TITLE) ? null
                : "title does not carry the component text: <" + title + ">";
    }

    // ----------------------------------------------------------------------------------------- //
    // MessageManager
    // ----------------------------------------------------------------------------------------- //

    /**
     * The cases that exercise {@link AbstractMessageManager#processAndSend}.
     * <p>
     * Which cases are declared depends on what the running server allows. Below 1.17 the manager
     * assembles bungee components only when the recipient is a {@link Player} and writes plain
     * legacy text to anything else, so reaching the events requires a {@link Player} the probe can
     * stand in for. Where the platform refuses to supply one, the cases that would need it are not
     * declared, and {@code msg.probe} asserts the refusal is the recorded one, so that their absence
     * is stated rather than assumed.
     * </p>
     *
     * @param map the case map to add to, in the order the cases are reported
     * @param ser the serializer that builds the item shown on hover
     */
    private static void addMessageCases(final @NotNull Map<String, Case> map,
                                        final @NotNull VersionedComponentSerializer ser) {
        final String tier = msgTierName();
        final @Nullable MsgProfile profile = MsgProfile.forTier(tier);

        // Not a skip, for the same reason tier.known is not one.
        map.put("msg.tierKnown", () -> profile != null ? null
                : "no expectations are declared for " + tier
                + "; add a MsgProfile entry rather than leaving this version unchecked");
        if (profile == null) { return; }

        map.put("msg.probe", () -> msgProbe(profile));
        if (profile.needsPlayer && playerProxyRefusal() != null) {
            map.put("msg.consoleFallback", () -> msgConsoleFallback());
            return;
        }

        // Where processAndSend goes, and whether the form every case below reads is the form it
        // sent. Without it the rest could be asserting on a string no client ever receives.
        map.put("msg.sendPath", () -> msgSendPath(profile));

        map.put("msg.click.runCommand", () -> msgClick(profile,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setClickRunCommand(MSG_RUN), "run_command", MSG_RUN));
        map.put("msg.click.suggestCommand", () -> msgClick(profile,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setClickSuggestCommand(MSG_SUGGEST), "suggest_command", MSG_SUGGEST));
        map.put("msg.click.openUrl", () -> msgClick(profile,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setClickOpenURL(MSG_URL), "open_url", MSG_URL));

        map.put("msg.hover.text", () -> msgExpect(profile,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setHoverText(MSG_TIP))
                .hasEvent(profile.bungee(), "hoverEvent", "hover_event")
                .has("\"action\":\"show_text\"", "no show_text action")
                .has(MSG_TIP, "tooltip text absent")
                .has(MSG_LABEL, "the replacement text is absent from the wire")
                .hasNot(MSG_PLACEHOLDER, "the placeholder reached the wire unreplaced")
                .result());

        // The display name is the claim. An item hover that carries the id alone renders as a plain
        // diamond sword, which is what a caller who set a custom name did not ask for, and every
        // structural check above it still passes in that state.
        map.put("msg.hover.item", () -> msgExpect(profile,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setHoverItem(testItem(ser)))
                .hasEvent(profile.bungee(), "hoverEvent", "hover_event")
                .has("show_item", "no show_item action")
                .has(ITEM_NAME, "the item's display name is absent, so the tooltip carries the id alone")
                .has(MSG_LABEL, "the replacement text is absent from the wire")
                .hasNot(MSG_PLACEHOLDER, "the placeholder reached the wire unreplaced")
                .result());

        // One action carrying both, because the two are written onto the same part and one
        // implementation of that part could hold either alone.
        map.put("msg.combined", () -> msgExpect(profile,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setClickSuggestCommand(MSG_SUGGEST).setHoverText(MSG_TIP))
                .hasEvent(profile.bungee(), "clickEvent", "click_event")
                .has("\"action\":\"suggest_command\"", "no suggest_command action")
                .has(MSG_SUGGEST, "click value absent")
                .hasEvent(profile.bungee(), "hoverEvent", "hover_event")
                .has("\"action\":\"show_text\"", "no show_text action")
                .has(MSG_TIP, "tooltip text absent")
                .hasNot(MSG_PLACEHOLDER, "the placeholder reached the wire unreplaced")
                .result());

        // The overload that also takes the translate flag. It is a second entry point into the same
        // implementation, and up to spigot-nms 1.2.36 it built its message without the actions it
        // was given, so every case above passed while any caller of this one lost every click and
        // hover. The flag is passed as true, which is the default the other overloads use, so the
        // only thing that differs between this case and msg.combined is which overload was called.
        map.put("msg.translateOverload", () -> msgExpect(profile, true,
                new Action(MSG_PLACEHOLDER, MSG_LABEL).setClickRunCommand(MSG_RUN).setHoverText(MSG_TIP))
                .hasEvent(profile.bungee(), "clickEvent", "click_event")
                .has("\"action\":\"run_command\"", "no run_command action")
                .has(MSG_RUN, "click value absent")
                .hasEvent(profile.bungee(), "hoverEvent", "hover_event")
                .has("\"action\":\"show_text\"", "no show_text action")
                .has(MSG_TIP, "tooltip text absent")
                .has(MSG_LABEL, "the replacement text is absent from the wire")
                .hasNot(MSG_PLACEHOLDER, "the placeholder reached the wire unreplaced")
                .result());
    }

    /** The implementation {@link AbstractMessageManager} dispatch selected on this server. */
    private static @NotNull String msgTierName() {
        return NmsAPI.getMessageManager().getClass().getSimpleName();
    }

    /**
     * Why this server refuses to proxy {@link Player}, or null when it does not refuse.
     */
    private static @Nullable String playerProxyRefusal() {
        try {
            Proxy.getProxyClass(Player.class.getClassLoader(), Player.class);
            return null;
        } catch (Throwable refused) {
            return refused.getClass().getName() + ": " + refused.getMessage();
        }
    }

    /**
     * That a probe of the shape this tier needs can be built, or that the refusal is the recorded one.
     * <p>
     * Stated in both directions. A refusal on a version that is supposed to allow one is a fault, and
     * so is an acceptance on a version recorded as refusing, because the cases excluded there would
     * then be excluded for no reason and nothing else would say so.
     * </p>
     */
    private static @Nullable String msgProbe(@NotNull MsgProfile profile) {
        if (!profile.needsPlayer) {
            try {
                Proxy.getProxyClass(CommandSender.class.getClassLoader(), CommandSender.class);
                return null;
            } catch (Throwable refused) {
                return "CommandSender cannot be proxied on " + NmsVersion.getMCVersion() + ": " + refused;
            }
        }
        boolean allowed = NmsVersion.getFormattedNmsInteger()
                >= NmsVersionParser.getFormattedNmsInteger(PLAYER_PROXY_FLOOR);
        @Nullable String refusal = playerProxyRefusal();
        if (allowed && refusal != null) {
            return "Player cannot be proxied on " + NmsVersion.getMCVersion()
                    + ", which is at or above " + PLAYER_PROXY_FLOOR + ": " + refusal;
        }
        if (!allowed && refusal == null) {
            return "Player can now be proxied on " + NmsVersion.getMCVersion()
                    + ", so the exclusion below " + PLAYER_PROXY_FLOOR
                    + " is stale and the msg.click and msg.hover cases can run here";
        }
        if (refusal != null && !refusal.contains("getHealth")) {
            return "Player was refused for a reason other than the recorded getHealth clash: " + refusal;
        }
        return null;
    }

    /**
     * Where {@code processAndSend} actually goes, and whether it agrees with what the cases read.
     */
    private static @Nullable String msgSendPath(@NotNull MsgProfile profile) {
        Wire wire = Wire.over(profile.face());
        send(wire, new Action(MSG_PLACEHOLDER, MSG_LABEL).setHoverText(MSG_TIP));
        if (!profile.transport.equals(wire.transport())) {
            return "processAndSend handed the platform a " + wire.transport()
                    + ", expected " + profile.transport;
        }
        if (profile.bungee()) { return null; }
        @Nullable String impl = wire.nativeClass();
        if (impl == null || !impl.startsWith("net.kyori.adventure.")) {
            return "the native send handed over " + impl + ", which is not the server's own Adventure";
        }
        return null;
    }

    private static @Nullable String msgClick(@NotNull MsgProfile profile, @NotNull Action action,
                                             @NotNull String wireAction, @NotNull String value) throws Exception {
        return msgExpect(profile, action)
                .hasEvent(profile.bungee(), "clickEvent", "click_event")
                .has("\"action\":\"" + wireAction + "\"", "no " + wireAction + " action")
                .has(value, "click value absent")
                .has(MSG_LABEL, "the replacement text is absent from the wire")
                .hasNot(MSG_PLACEHOLDER, "the placeholder reached the wire unreplaced")
                .result();
    }

    /**
     * What a console recipient receives on a tier that builds components only for a {@link Player}.
     * <p>
     * The events are unreachable here by construction, so the contract left to assert is the one the
     * implementation documents for everything that is not a player: the replacements are applied and
     * the surrounding text survives.
     * </p>
     */
    private static @Nullable String msgConsoleFallback() {
        Wire wire = Wire.over(CommandSender.class);
        send(wire, new Action(MSG_PLACEHOLDER, MSG_LABEL).setClickRunCommand(MSG_RUN).setHoverText(MSG_TIP));
        if (!"LEGACY_STRING".equals(wire.transport())) {
            return "processAndSend handed the console a " + wire.transport() + ", expected LEGACY_STRING";
        }
        @Nullable String legacy = wire.legacy();
        if (legacy == null) { return "processAndSend sent nothing at all"; }
        if (legacy.contains(MSG_PLACEHOLDER)) {
            return "the placeholder was not replaced: <" + readable(legacy) + ">";
        }
        if (!legacy.contains(MSG_LABEL)) {
            return "the replacement text is absent: <" + readable(legacy) + ">";
        }
        if (!legacy.contains(MSG_HEAD) || !legacy.contains(MSG_TAIL)) {
            return "the text around the placeholder was lost: <" + readable(legacy) + ">";
        }
        return null;
    }

    private static void send(@NotNull Wire wire, @NotNull Action action) {
        NmsAPI.getMessageManager().processAndSend(wire.sender, MSG_LINE, action);
    }

    /** The same send through the overload that also carries the translate flag. */
    private static void send(@NotNull Wire wire, boolean translate, @NotNull Action action) {
        NmsAPI.getMessageManager().processAndSend(wire.sender, MSG_LINE, translate, action);
    }

    private static @NotNull Expect msgExpect(@NotNull MsgProfile profile, @NotNull Action action) throws Exception {
        Wire wire = Wire.over(profile.face());
        send(wire, action);
        return expectWire(msgWire(wire, profile));
    }

    private static @NotNull Expect msgExpect(@NotNull MsgProfile profile, boolean translate,
                                             @NotNull Action action) throws Exception {
        Wire wire = Wire.over(profile.face());
        send(wire, translate, action);
        return expectWire(msgWire(wire, profile));
    }

    /** The serialized form of what {@code processAndSend} handed this server's platform. */
    private static @NotNull String msgWire(@NotNull Wire wire, @NotNull MsgProfile profile) throws Exception {
        if (profile.bungee()) {
            @Nullable BaseComponent[] captured = wire.captured();
            if (captured == null) {
                throw new IllegalStateException("processAndSend sent a " + wire.transport()
                        + " where " + profile.tier + " is expected to send bungee components");
            }
            return bungeeJson(captured);
        }
        @Nullable Object component = wire.nativeObject();
        if (component == null) {
            throw new IllegalStateException("processAndSend sent a " + wire.transport()
                    + " where " + profile.tier + " is expected to send a native component");
        }
        return adventureJson(component);
    }

    /**
     * A component the server's own Adventure received, serialized by that same Adventure.
     * <p>
     * Reached reflectively because this file is compiled against the 1.8.8 API, which has no
     * Adventure at all, and is loaded on servers that have none either. Resolving the serializer by
     * name confines it to the tiers that send through it.
     * </p>
     */
    private static @NotNull String adventureJson(@NotNull Object component) throws Exception {
        Class<?> api = Class.forName("net.kyori.adventure.text.serializer.gson.GsonComponentSerializer");
        Object serializer = api.getMethod("gson").invoke(null);
        for (Method method : api.getMethods()) {
            if (!"serialize".equals(method.getName())) { continue; }
            if (method.getParameterTypes().length != 1) { continue; }
            method.setAccessible(true);
            return String.valueOf(method.invoke(serializer, component));
        }
        throw new IllegalStateException("nothing on " + api.getName() + " serializes a component");
    }

    // ----------------------------------------------------------------------------------------- //
    // MessageManager, for callers outside this command
    // ----------------------------------------------------------------------------------------- //

    /**
     * Why the {@link AbstractMessageManager} cases cannot run on this server, or null when they can.
     *
     * @return the reason, suitable for reporting a documented refusal rather than a failure
     */
    static @Nullable String messageManagerUnreachable() {
        String tier = msgTierName();
        @Nullable MsgProfile profile = MsgProfile.forTier(tier);
        // An unknown tier is a failure rather than a refusal, and msg.tierKnown is what reports it.
        if (profile == null || !profile.needsPlayer) { return null; }
        @Nullable String refusal = playerProxyRefusal();
        if (refusal == null) { return null; }
        return tier + " builds components only for a Player, and " + NmsVersion.getMCVersion()
                + " does not allow one to be proxied, so what it sends cannot be read: " + refusal;
    }

    /**
     * Runs the {@link AbstractMessageManager} cases on their own.
     *
     * @return one entry per failing case, empty when every case passed
     */
    static @NotNull List<String> messageManagerProblems() {
        Map<String, Case> map = new LinkedHashMap<String, Case>();
        addMessageCases(map, NmsAPI.getVersionedComponentSerializer());
        List<String> problems = new ArrayList<String>();
        for (Map.Entry<String, Case> entry : map.entrySet()) {
            @Nullable String problem = describe(entry.getValue());
            if (problem != null) { problems.add(entry.getKey() + ": " + problem); }
        }
        return problems;
    }

    // ----------------------------------------------------------------------------------------- //
    // Player mode
    // ----------------------------------------------------------------------------------------- //

    /** Sends the cases a wire assertion cannot judge, which is whether the client draws them. */
    private static void showTo(@NotNull Player player, @NotNull VersionedComponentSerializer ser,
                               @Nullable Profile profile) {
        ser.fromMiniMessage("<gray>texttest: hover 1, 6 and part of 9, click 2, 3, 4, 5 and part of 9,"
                + " then close the menu.").sendTo(player);
        ser.fromMiniMessage("<yellow>1. ").append(
                ser.fromMiniMessage("<white>[hover me]").hover(ser.fromMiniMessage("<aqua>Tooltip rendered."))
        ).sendTo(player);
        ser.fromMiniMessage("<yellow>2. ").append(
                ser.fromMiniMessage("<white>[run /kc version]").click(ClickAction.RUN_COMMAND, "/kc version")
        ).sendTo(player);
        ser.fromMiniMessage("<yellow>3. ").append(
                ser.fromMiniMessage("<white>[suggest a command]").click(ClickAction.SUGGEST_COMMAND, "/kc texttest")
        ).sendTo(player);
        ser.fromMiniMessage("<yellow>4. ").append(
                ser.fromMiniMessage("<white>[open a url]").click(ClickAction.OPEN_URL, "https://luxiouslabs.net")
        ).sendTo(player);
        if (profile != null && profile.copyToClipboard) {
            ser.fromMiniMessage("<yellow>5. ").append(
                    ser.fromMiniMessage("<white>[copy to clipboard]").click(ClickAction.COPY_TO_CLIPBOARD, COPY_VALUE)
            ).sendTo(player);
        } else {
            ser.fromMiniMessage("<yellow>5. <dark_gray>copy to clipboard needs 1.16 or newer.").sendTo(player);
        }
        if (profile != null && profile.hoverItem) {
            ser.fromMiniMessage("<yellow>6. ").append(
                    ser.fromMiniMessage("<white>[hover the item, it must show a name and two lore lines]")
                            .hoverItem(testItem(ser))
            ).sendTo(player);
        } else {
            ser.fromMiniMessage("<yellow>6. <dark_gray>item hover is unavailable on this version.").sendTo(player);
        }
        ser.fromMiniMessage("<yellow>7. <bold>bold</bold> <italic>italic</italic> <underlined>underlined</underlined>"
                + " <strikethrough>struck</strikethrough> <obfuscated>hidden</obfuscated>").sendTo(player);
        ser.fromMiniMessage("<yellow>8. <" + HEX + ">this line is " + HEX
                + ", pink from 1.16 and the nearest named colour below it.").sendTo(player);
        showMessageManagerTo(player, ser);
        player.openInventory(ser.fromMiniMessage("<green>" + MENU_TITLE).createInventory(HOLDER, 9));
    }

    /**
     * The five action kinds and the combined case through {@link AbstractMessageManager}, for the
     * rendering check.
     * <p>
     * Sent as one line with six placeholders because that is the shape a caller writes, and because
     * the bungee implementation splits a line on each placeholder in turn, which a line carrying one
     * placeholder never exercises.
     * </p>
     */
    private static void showMessageManagerTo(@NotNull Player player, @NotNull VersionedComponentSerializer ser) {
        ser.fromMiniMessage("<yellow>9. <gray>the line below is built by MessageManager, not by the"
                + " component API above. Hover 4, 5 and 6, click 1, 2, 3 and 6.").sendTo(player);
        NmsAPI.getMessageManager().processAndSend(player,
                "   <1> <2> <3> <4> <5> <6>.",
                new Action("<1>", "&a[run]").setClickRunCommand("/kc version"),
                new Action("<2>", "&b[suggest]").setClickSuggestCommand("/kc texttest"),
                new Action("<3>", "&c[url]").setClickOpenURL("https://luxiouslabs.net"),
                new Action("<4>", "&d[hover text]").setHoverText("&bTooltip rendered."),
                new Action("<5>", "&e[hover item]").setHoverItem(testItem(ser)),
                new Action("<6>", "&f[both]").setClickSuggestCommand("/kc texttest").setHoverText("&bTooltip rendered.")
        );
    }

    // ----------------------------------------------------------------------------------------- //
    // The sent form
    // ----------------------------------------------------------------------------------------- //

    /**
     * The serialized form a client on this server receives.
     * <p>
     * Below 1.18.2 that is not the same thing as {@code serializeJson()}. Those tiers send through
     * bungee-chat, which parses the serialized form and silently drops any event key it does not
     * recognise, so what a client receives is decided after the library is finished with it. This
     * reads the far side of that boundary, using the tier's own serializer instance.
     * </p>
     */
    private static @NotNull String wireFor(@NotNull VersionedComponent component, @NotNull Profile profile)
            throws Exception {
        if (profile.bungee()) { return bungeeJson(bungeeWire(component)); }
        // From 1.18.2 the component itself is handed to the server's own Adventure with nothing in
        // between, and serializeJson is that same Adventure writing that same component.
        return component.serializeJson();
    }

    /**
     * What the tier hands to bungee-chat, taken from the tier's own {@code SERIALIZER} rather than
     * rebuilt here. A serializer configured alongside this file could be configured differently, and
     * a check that compares one implementation to another cannot see a fault the two share.
     */
    private static @NotNull BaseComponent[] bungeeWire(@NotNull VersionedComponent component) throws Exception {
        Class<?> impl = component.getClass();
        @Nullable Field field = null;
        // append() on one tier returns an anonymous subclass, so the field may be inherited.
        for (Class<?> c = impl; c != null && field == null; c = c.getSuperclass()) {
            try {
                field = c.getDeclaredField("SERIALIZER");
            } catch (NoSuchFieldException notHere) {
                field = null;
            }
        }
        if (field == null) {
            throw new IllegalStateException(impl.getName()
                    + " has no SERIALIZER, so the form it sends cannot be read");
        }
        field.setAccessible(true);
        Object serializer = field.get(null);
        Method shaded = impl.getMethod("shadedComponent");
        shaded.setAccessible(true);
        Object adventure = shaded.invoke(component);
        for (Method method : serializer.getClass().getMethods()) {
            if (!"serialize".equals(method.getName())) { continue; }
            if (method.getParameterTypes().length != 1) { continue; }
            if (!BaseComponent[].class.equals(method.getReturnType())) { continue; }
            method.setAccessible(true);
            return (BaseComponent[]) method.invoke(serializer, adventure);
        }
        throw new IllegalStateException("nothing on " + serializer.getClass().getName()
                + " serializes to BaseComponent[]");
    }

    /**
     * Serialized by the receiving server's own bungee-chat, which is the point.
     * <p>
     * The events present here are the ones that server understood. An event written under a key name
     * it does not read is already gone, which is how {@code click()} shipped dead for nine releases
     * while the library's own view of the component still held it.
     * </p>
     * <p>
     * Kept in its own method so that {@code net.md_5.bungee} is resolved only on the versions that
     * send through it.
     * </p>
     */
    private static @NotNull String bungeeJson(@NotNull BaseComponent[] components) {
        return ComponentSerializer.toString(components);
    }

    /**
     * Where {@code sendTo} actually goes, and whether it agrees with what every other case reads.
     */
    private static @Nullable String sendPath(@NotNull VersionedComponentSerializer ser, @NotNull Profile profile)
            throws Exception {
        VersionedComponent component = ser.fromPlainText("SENDPATH").hover(ser.fromPlainText("SENDPATHTIP"));
        Wire sent = Wire.send(component);
        if (!profile.consoleSend.equals(sent.transport())) {
            return "sendTo handed the console a " + sent.transport() + ", expected " + profile.consoleSend;
        }
        if (!profile.bungee()) {
            @Nullable String impl = sent.nativeClass();
            if (impl == null || !impl.startsWith("net.kyori.adventure.")) {
                return "the native send handed over " + impl + ", which is not the server's own Adventure";
            }
            return null;
        }
        // One tier writes legacy text to anything that is not a player, so the send cannot always be
        // read as a wire form. Confirming the two agree is what makes the tier's own serializer a
        // valid stand-in for the send below.
        BaseComponent[] read = bungeeWire(component);
        @Nullable BaseComponent[] captured = sent.captured();
        if (captured != null) {
            String fromSend = bungeeJson(captured);
            String fromSerializer = bungeeJson(read);
            return fromSend.equals(fromSerializer) ? null
                    : "sendTo produced " + clip(fromSend) + " but the tier's serializer produced " + clip(fromSerializer);
        }
        @Nullable String legacy = sent.legacy();
        if (legacy == null) { return "sendTo produced nothing at all"; }
        String fromSerializer = new TextComponent(read).toLegacyText();
        return legacy.equals(fromSerializer) ? null
                : "sendTo wrote <" + legacy + "> but the tier's serializer produced <" + fromSerializer + ">";
    }

    /**
     * A sender that keeps whatever it is handed instead of delivering it.
     * <p>
     * A {@link CommandSender} is enough for everything {@link VersionedComponent} sends. The bungee
     * side of {@link AbstractMessageManager} branches on the recipient being a {@link Player} and
     * needs the probe to be one, which is possible from 1.9 onward and not before: up to 1.8.9
     * Bukkit's {@code Damageable} declares {@code getHealth()} twice with incompatible primitive
     * return types, and {@link Proxy} rejects the interface outright. See
     * {@link #PLAYER_PROXY_FLOOR}.
     * </p>
     */
    private static final class Wire implements InvocationHandler {
        private final CapturingSpigot spigot = new CapturingSpigot();
        private final CommandSender sender;
        private @Nullable Object nativeComponent;
        private @Nullable String legacy;

        private Wire(@NotNull Class<?> face) {
            this.sender = (CommandSender) Proxy.newProxyInstance(
                    face.getClassLoader(), new Class<?>[]{face}, this);
        }

        /**
         * A probe that stands in for {@code face}.
         *
         * @param face {@link CommandSender}, or {@link Player} where the code under test branches on
         *             the recipient being one
         */
        private static @NotNull Wire over(@NotNull Class<?> face) {
            return new Wire(face);
        }

        private static @NotNull Wire send(@NotNull VersionedComponent component) {
            Wire wire = new Wire(CommandSender.class);
            component.sendTo(wire.sender);
            return wire;
        }

        @Override
        public @Nullable Object invoke(@NotNull Object proxy, @NotNull Method method, Object[] args) {
            String name = method.getName();
            if ("spigot".equals(name)) { return this.spigot; }
            if ("sendMessage".equals(name) && args != null && args.length == 1) {
                if (args[0] instanceof String) { this.legacy = (String) args[0]; }
                else if (args[0] instanceof String[]) { this.legacy = String.join("\n", (String[]) args[0]); }
                else { this.nativeComponent = args[0]; }
                return null;
            }
            if ("getName".equals(name) || "toString".equals(name)) { return "TextTestWireProbe"; }
            if ("hashCode".equals(name)) { return System.identityHashCode(proxy); }
            if ("equals".equals(name)) { return proxy == args[0]; }
            // Every send path calls spigot() or sendMessage and nothing else. A call to anything
            // further is a change in how components reach clients, and is worth stopping on.
            throw new UnsupportedOperationException("the texttest wire probe does not implement " + method);
        }

        private @NotNull String transport() {
            if (this.spigot.captured != null) { return "BUNGEE"; }
            if (this.nativeComponent != null) { return "NATIVE"; }
            if (this.legacy != null) { return "LEGACY_STRING"; }
            return "NOTHING_SENT";
        }

        private @Nullable BaseComponent[] captured() {
            return this.spigot.captured;
        }

        private @Nullable String legacy() {
            return this.legacy;
        }

        private @Nullable String nativeClass() {
            return this.nativeComponent == null ? null : this.nativeComponent.getClass().getName();
        }

        private @Nullable Object nativeObject() {
            return this.nativeComponent;
        }
    }

    /**
     * Catches what {@code spigot().sendMessage(BaseComponent...)} was given.
     * <p>
     * Extends {@code Player.Spigot} rather than {@code CommandSender.Spigot} because the latter does
     * not exist on 1.8, and from 1.9 the former is a subclass of it.
     * </p>
     */
    private static final class CapturingSpigot extends Player.Spigot {
        private @Nullable BaseComponent[] captured;

        @Override
        public void sendMessage(BaseComponent component) {
            this.captured = new BaseComponent[]{component};
        }

        @Override
        public void sendMessage(BaseComponent... components) {
            this.captured = components;
        }
    }

    // ----------------------------------------------------------------------------------------- //
    // Expectations
    // ----------------------------------------------------------------------------------------- //

    /** What a tier is expected to emit, keyed on the implementation dispatch actually selected. */
    private static final class Profile {
        private final String tier;
        private final String transport;
        private final String consoleSend;
        private final boolean hexOnWire;
        private final boolean copyToClipboard;
        private final boolean hoverItem;
        /** Whether the tier hands the display name to the server as a component rather than as text. */
        private final boolean nativeItemName;
        /** The same question for lore, which one tier answers differently from the display name. */
        private final boolean nativeItemLore;

        private Profile(String tier, String transport, String consoleSend,
                        boolean hexOnWire, boolean copyToClipboard, boolean hoverItem,
                        boolean nativeItemName, boolean nativeItemLore) {
            this.tier = tier;
            this.transport = transport;
            this.consoleSend = consoleSend;
            this.hexOnWire = hexOnWire;
            this.copyToClipboard = copyToClipboard;
            this.hoverItem = hoverItem;
            this.nativeItemName = nativeItemName;
            this.nativeItemLore = nativeItemLore;
        }

        private boolean bungee() {
            return "BUNGEE".equals(this.transport);
        }

        /**
         * The expectations for one tier, or null if none are declared.
         * <p>
         * Keyed on the implementation the running server dispatched to rather than on a version
         * comparison. A second copy of the version ladder would be free to disagree with the one in
         * {@code VersionedComponentSerializer}, and nothing would report that it had.
         * </p>
         */
        private static @Nullable Profile forTier(@NotNull String tier) {
            for (Profile profile : PROFILES) {
                if (profile.tier.equals(tier)) { return profile; }
            }
            return null;
        }

        private static final List<Profile> PROFILES = Arrays.asList(
                // 1.8 to 1.11.2. No RGB, no copy to clipboard, item hover assembled from item NBT.
                // Alone among the tiers it serializes only for players and writes legacy text to
                // anything else, which is why the console send differs here.
                new Profile("VersionedComponent_1_11_R1", "BUNGEE", "LEGACY_STRING", false, false, true, false, false),
                // 1.12 to 1.15.2.
                new Profile("VersionedComponent_1_15_R1", "BUNGEE", "BUNGEE", false, false, true, false, false),
                // 1.16.x. RGB and copy to clipboard arrive here.
                new Profile("VersionedComponent_1_16_R3", "BUNGEE", "BUNGEE", true, true, true, false, false),
                // 1.17 to 1.18.1. Sends through bungee-chat like the tiers below it, and builds
                // its item hover from item NBT read by the 1.17 and 1.18.1 modules.
                new Profile("VersionedComponent_1_17_R1", "BUNGEE", "BUNGEE", true, true, true, false, false),
                // 1.18.2 upward. The server's own Adventure receives the component directly.
                // This tier alone splits the two item paths: the display name is written as
                // section-coded text because customName() only arrived in Paper 1.21.4, while lore
                // is already handed over as components.
                new Profile("VersionedComponent_1_18_R2", "NATIVE", "NATIVE", true, true, true, false, true),
                new Profile("VersionedComponent_1_21_4", "NATIVE", "NATIVE", true, true, true, true, true),
                new Profile("VersionedComponent_LATEST", "NATIVE", "NATIVE", true, true, true, true, true)
        );
    }

    /**
     * What one {@link AbstractMessageManager} implementation is expected to emit.
     * <p>
     * Keyed separately from {@link Profile} because the two ladders do not agree. A 1.17.1 server
     * sends {@link VersionedComponent} through bungee-chat and its messages through the server's own
     * Adventure, so reusing the component tier here would state the wrong transport for that version
     * and for no other.
     * </p>
     */
    private static final class MsgProfile {
        private final String tier;
        private final String transport;
        /**
         * Whether the implementation builds components only for a {@link Player}.
         * <p>
         * Distinct from the transport it happens to coincide with. The bungee implementation
         * branches on {@code instanceof Player} and writes legacy text to everything else, and that
         * branch, not the component library it uses, is what the probe has to satisfy.
         * </p>
         */
        private final boolean needsPlayer;

        private MsgProfile(String tier, String transport, boolean needsPlayer) {
            this.tier = tier;
            this.transport = transport;
            this.needsPlayer = needsPlayer;
        }

        private boolean bungee() {
            return "BUNGEE".equals(this.transport);
        }

        private @NotNull Class<?> face() {
            return this.needsPlayer ? Player.class : CommandSender.class;
        }

        private static @Nullable MsgProfile forTier(@NotNull String tier) {
            for (MsgProfile profile : PROFILES) {
                if (profile.tier.equals(tier)) { return profile; }
            }
            return null;
        }

        private static final List<MsgProfile> PROFILES = Arrays.asList(
                // 1.8 to 1.16.5. Bungee components assembled by hand, with the item hover taken from
                // the item NBT the pre-1.17 item text provider produces.
                new MsgProfile("MessageManager_1_8_R1", "BUNGEE", true),
                // 1.17 upward, every version of it. The server's own Adventure receives the
                // component directly, whatever the recipient is.
                new MsgProfile("MessageManager_1_17_R1", "NATIVE", false),
                // 26.x, which the ladder sends here from spigot-nms 1.2.37. The same source as the
                // entry above, compiled against the Paper those servers run.
                new MsgProfile("MessageManager_LATEST", "NATIVE", false)
        );
    }

    /** Accumulates every problem with one component rather than reporting only the first. */
    private static final class Expect {
        /** Null where the wire form was captured from a send rather than serialized from a component. */
        private final @Nullable VersionedComponent component;
        private final String wire;
        private final String flat;
        private final List<String> problems = new ArrayList<String>();

        private Expect(@Nullable VersionedComponent component, @NotNull String wire) {
            this.component = component;
            this.wire = wire;
            this.flat = flat(wire);
        }

        /**
         * Hex is matched without regard to case, because the case is not part of the colour. The
         * serializers do not agree on it: MiniMessage is given lower case and the wire comes back
         * upper case on every tier that carries RGB at all.
         */
        private @NotNull Expect hasHex(@NotNull String why) {
            if (!this.flat.toLowerCase().contains(HEX)) { this.problems.add(why + ", no " + HEX); }
            return this;
        }

        private @NotNull Expect hasNoHex(@NotNull String why) {
            if (this.flat.toLowerCase().contains(HEX)) { this.problems.add(why + ", found " + HEX); }
            return this;
        }

        private @NotNull Expect text(@NotNull String expected) {
            if (this.component == null) {
                throw new IllegalStateException("text() needs the component, and this Expect holds"
                        + " only the captured wire form");
            }
            String actual = this.component.serializePlainText();
            if (!expected.equals(actual)) {
                this.problems.add("plain text is <" + actual + ">, expected <" + expected + ">");
            }
            return this;
        }

        private @NotNull Expect has(@NotNull String needle, @NotNull String why) {
            if (!this.flat.contains(needle)) { this.problems.add(why + ", no " + needle); }
            return this;
        }

        private @NotNull Expect hasNot(@NotNull String needle, @NotNull String why) {
            if (this.flat.contains(needle)) { this.problems.add(why + ", found " + needle); }
            return this;
        }

        /**
         * Exactly one occurrence, which is how an item nested inside its own tag compound is caught.
         * <p>
         * Absence cannot express it: the id belongs on the wire once, and what is wrong is a second
         * copy of it inside the tag. Matching the textual shape of the nesting instead would have to
         * know which member the running version's NBT writer puts first, and the mappings up to
         * 1.16.5 do not agree with those of 1.17 and 1.18.1.
         * </p>
         */
        private @NotNull Expect hasOnce(@NotNull String needle, @NotNull String why) {
            int found = 0;
            for (int at = this.flat.indexOf(needle); at >= 0; at = this.flat.indexOf(needle, at + 1)) {
                found++;
            }
            if (found != 1) {
                this.problems.add(why + ", " + needle + " appears " + found + " times, expected once");
            }
            return this;
        }

        /**
         * The event key, under the name the receiving end reads.
         * <p>
         * Below 1.18.2 that name is fixed: the bungee-chat bundled with those versions reads
         * {@code hoverEvent} and {@code clickEvent} and discards anything else. From 1.18.2 the
         * server serializes with its own Adventure, which spells the key one way up to 1.21.4 and the
         * other from 1.21.5, and both are correct there because both are that server's own.
         * </p>
         */
        private @NotNull Expect hasEvent(boolean bungee, @NotNull String legacyKey, @NotNull String modernKey) {
            if (bungee) {
                return has("\"" + legacyKey + "\"", "the event is not under a key this server's bungee-chat reads");
            }
            if (!this.flat.contains("\"" + legacyKey + "\"") && !this.flat.contains("\"" + modernKey + "\"")) {
                this.problems.add("no " + legacyKey + " or " + modernKey + " member");
            }
            return this;
        }

        private @Nullable String result() {
            if (this.problems.isEmpty()) { return null; }
            return String.join("; ", this.problems) + " | wire=" + clip(this.wire);
        }
    }

    private static @NotNull Expect expect(@NotNull VersionedComponent component, @NotNull Profile profile)
            throws Exception {
        return new Expect(component, wireFor(component, profile));
    }

    /** An expectation over a wire form that was captured from a send rather than serialized here. */
    private static @NotNull Expect expectWire(@NotNull String wire) {
        return new Expect(null, wire);
    }

    // ----------------------------------------------------------------------------------------- //
    // Plumbing
    // ----------------------------------------------------------------------------------------- //

    private static void run(@NotNull Results results, @NotNull String name, @NotNull Case body) {
        @Nullable String problem = describe(body);
        if (problem == null) { results.pass(name); } else { results.fail(name, problem); }
    }

    /** Why one case failed, or null when it passed. A throw is a failure and reports its root cause. */
    private static @Nullable String describe(@NotNull Case body) {
        try {
            return body.run();
        } catch (Throwable thrown) {
            Throwable root = thrown;
            while (root.getCause() != null) { root = root.getCause(); }
            return root.getClass().getName() + ": " + root.getMessage();
        }
    }

    private static @Nullable String sameText(@NotNull String expected, @NotNull String actual) {
        return expected.equals(actual) ? null : "expected <" + expected + "> but got <" + actual + ">";
    }

    /** Structural matching ignores whitespace, which no serializer is obliged to place identically. */
    private static @NotNull String flat(@NotNull String json) {
        return json.replace(" ", "");
    }

    /**
     * Section codes spelled out, so that logged evidence can be read.
     * <p>
     * The console appender strips the section character itself on some versions, which turns a
     * legacy string carrying colour and italic into one that appears to carry neither.
     * </p>
     */
    private static @NotNull String readable(@NotNull String text) {
        return text.replace("\u00a7", "\\u00a7");
    }

    private static @NotNull String clip(@NotNull String wire) {
        return wire.length() <= 700 ? wire : wire.substring(0, 700) + "...(" + wire.length() + " chars)";
    }

    private static @NotNull String tierName(@NotNull VersionedComponentSerializer ser) {
        return ser.fromPlainText("x").getClass().getSimpleName();
    }

    /** One case. Returns the reason it failed, or null when it passed. */
    private interface Case {
        @Nullable String run() throws Exception;
    }

    /** A call that is expected to throw. */
    private interface Body {
        void run() throws Exception;
    }

    /** Pass and fail lines as they are written, and the counts the summary reports. */
    private static final class Results {
        private final Logger logger;
        private final List<String> failed = new ArrayList<String>();
        private int passed = 0;

        private Results(@NotNull Logger logger) {
            this.logger = logger;
        }

        private void pass(@NotNull String name) {
            this.logger.info(PREFIX + "PASS " + name);
            this.passed++;
        }

        private void fail(@NotNull String name, @NotNull String why) {
            this.logger.severe(PREFIX + "FAIL " + name + ": " + why);
            this.failed.add(name);
        }

        private boolean ok() {
            return this.failed.isEmpty();
        }

        private int total() {
            return this.passed + this.failed.size();
        }
    }
}
