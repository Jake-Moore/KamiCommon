package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.ClickAction;
import com.kamikazejam.kamicommon.nms.text.TextDecoration;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.util.VersionedComponentUtil;
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
    private static final String MENU_TITLE = "KCTEXTTESTMENU";

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
            for (Map.Entry<String, Case> entry : cases(ser, profile).entrySet()) {
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
     * @return case name to case
     */
    private static @NotNull Map<String, Case> cases(final @NotNull VersionedComponentSerializer ser,
                                                    final @NotNull Profile profile) {
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
                    .hasEvent(profile, "hoverEvent", "hover_event")
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
                    .hasEvent(profile, "hoverEvent", "hover_event")
                    .has("show_item", "no show_item action")
                    .has(ITEM_NAME, "display name absent")
                    .has(LORE_1, "first lore line absent")
                    .has(LORE_2, "second lore line absent")
                    // The whole item NBT passed where the tag belongs. It renders as the bare item
                    // with no name and no lore, and every payload above is still present.
                    .hasNot("tag:{id:", "item NBT nested under a second tag key")
                    .hasNot("\"tag\":\"{id:", "item NBT nested under a second tag key")
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

        return map;
    }

    private static @Nullable String click(@NotNull VersionedComponentSerializer ser, @NotNull Profile profile,
                                          @NotNull ClickAction action, @NotNull String wireAction,
                                          @NotNull String value) throws Exception {
        VersionedComponent c = ser.fromPlainText("CLICKANCHOR").click(action, value);
        return expect(c, profile)
                .hasEvent(profile, "clickEvent", "click_event")
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
    // Player mode
    // ----------------------------------------------------------------------------------------- //

    /** Sends the cases a wire assertion cannot judge, which is whether the client draws them. */
    private static void showTo(@NotNull Player player, @NotNull VersionedComponentSerializer ser,
                               @Nullable Profile profile) {
        ser.fromMiniMessage("<gray>texttest: hover 1 and 6, click 2, 3, 4 and 5, then close the menu.").sendTo(player);
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
        player.openInventory(ser.fromMiniMessage("<green>" + MENU_TITLE).createInventory(HOLDER, 9));
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
     * A sender that keeps whatever {@code sendTo} hands the platform instead of delivering it.
     * <p>
     * A {@link CommandSender} and not a {@link Player}, which cannot be proxied at all: Bukkit's
     * {@code Damageable} declares {@code getHealth()} twice with incompatible primitive return types
     * up to 1.11, and {@link Proxy} rejects the interface outright.
     * </p>
     */
    private static final class Wire implements InvocationHandler {
        private final CapturingSpigot spigot = new CapturingSpigot();
        private final CommandSender sender;
        private @Nullable Object nativeComponent;
        private @Nullable String legacy;

        private Wire() {
            this.sender = (CommandSender) Proxy.newProxyInstance(
                    CommandSender.class.getClassLoader(), new Class<?>[]{CommandSender.class}, this);
        }

        private static @NotNull Wire send(@NotNull VersionedComponent component) {
            Wire wire = new Wire();
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

        private Profile(String tier, String transport, String consoleSend,
                        boolean hexOnWire, boolean copyToClipboard, boolean hoverItem) {
            this.tier = tier;
            this.transport = transport;
            this.consoleSend = consoleSend;
            this.hexOnWire = hexOnWire;
            this.copyToClipboard = copyToClipboard;
            this.hoverItem = hoverItem;
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
                new Profile("VersionedComponent_1_11_R1", "BUNGEE", "LEGACY_STRING", false, false, true),
                // 1.12 to 1.15.2.
                new Profile("VersionedComponent_1_15_R1", "BUNGEE", "BUNGEE", false, false, true),
                // 1.16.x. RGB and copy to clipboard arrive here.
                new Profile("VersionedComponent_1_16_R3", "BUNGEE", "BUNGEE", true, true, true),
                // 1.17 to 1.18.1. No NBT source above 1.16.5 and no native Adventure until 1.18.2, so
                // an item hover cannot be built and must refuse rather than show the wrong item.
                new Profile("VersionedComponent_1_17_R1", "BUNGEE", "BUNGEE", true, true, false),
                // 1.18.2 upward. The server's own Adventure receives the component directly.
                new Profile("VersionedComponent_1_18_R2", "NATIVE", "NATIVE", true, true, true),
                new Profile("VersionedComponent_1_21_4", "NATIVE", "NATIVE", true, true, true),
                new Profile("VersionedComponent_LATEST", "NATIVE", "NATIVE", true, true, true)
        );
    }

    /** Accumulates every problem with one component rather than reporting only the first. */
    private static final class Expect {
        private final VersionedComponent component;
        private final String wire;
        private final String flat;
        private final List<String> problems = new ArrayList<String>();

        private Expect(@NotNull VersionedComponent component, @NotNull Profile profile) throws Exception {
            this.component = component;
            this.wire = wireFor(component, profile);
            this.flat = flat(this.wire);
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
         * The event key, under the name the receiving end reads.
         * <p>
         * Below 1.18.2 that name is fixed: the bungee-chat bundled with those versions reads
         * {@code hoverEvent} and {@code clickEvent} and discards anything else. From 1.18.2 the
         * server serializes with its own Adventure, which spells the key one way up to 1.21.4 and the
         * other from 1.21.5, and both are correct there because both are that server's own.
         * </p>
         */
        private @NotNull Expect hasEvent(@NotNull Profile profile, @NotNull String legacyKey, @NotNull String modernKey) {
            if (profile.bungee()) {
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
        return new Expect(component, profile);
    }

    // ----------------------------------------------------------------------------------------- //
    // Plumbing
    // ----------------------------------------------------------------------------------------- //

    private static void run(@NotNull Results results, @NotNull String name, @NotNull Case body) {
        try {
            @Nullable String problem = body.run();
            if (problem == null) { results.pass(name); } else { results.fail(name, problem); }
        } catch (Throwable thrown) {
            Throwable root = thrown;
            while (root.getCause() != null) { root = root.getCause(); }
            results.fail(name, root.getClass().getName() + ": " + root.getMessage());
        }
    }

    private static @Nullable String sameText(@NotNull String expected, @NotNull String actual) {
        return expected.equals(actual) ? null : "expected <" + expected + "> but got <" + actual + ">";
    }

    /** Structural matching ignores whitespace, which no serializer is obliged to place identically. */
    private static @NotNull String flat(@NotNull String json) {
        return json.replace(" ", "");
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
