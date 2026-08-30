package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves every NMS provider and reports which implementation this server got.
 * <p>
 * {@code /kc nmstest} exercises the providers but needs a player, so it cannot be used to check a
 * server version without someone logging in. This runs from the console, which is what makes the
 * version matrix checkable: every dispatch ladder is evaluated, every provider is constructed, and
 * the class that comes back is printed by name.
 * </p>
 * <p>
 * It is aimed at one failure in particular. Each {@code versions/*} module targets the JVM its own
 * Minecraft version required, so a ladder that sends an old server to a module built for a newer one
 * throws {@link UnsupportedClassVersionError} the moment that provider is touched. Before this
 * existed, the only way to find that was to boot the server and wait for something to use the
 * provider. Here every provider is touched on purpose, and the failure is reported against the name
 * of the one that broke rather than surfacing later from unrelated code.
 * </p>
 */
@SuppressWarnings("SpellCheckingInspection")
public class CmdNmsProviders extends KamiCommand {

    public CmdNmsProviders() {
        addAliases("nmsproviders", "nmsp");
        addRequirements(RequirementHasPerm.get("kamicommon.command.nmsproviders"));
    }

    /** Each provider, and how to resolve it. Every entry constructs, so every ladder is evaluated. */
    private static @NotNull Map<String, Resolver> providers() {
        Map<String, Resolver> map = new LinkedHashMap<String, Resolver>();
        map.put("blockUtil", new Resolver() { public Object get() { return NmsAPI.getBlockUtil(); } });
        map.put("chatColor", new Resolver() { public Object get() { return NmsAPI.getChatColorProvider().get(); } });
        map.put("commandMapModifier", new Resolver() { public Object get() { return NmsAPI.getCommandMapModifierProvider().get(); } });
        map.put("componentLoggerAdapter", new Resolver() { public Object get() { return NmsAPI.getComponentLoggerAdapter(); } });
        map.put("enchantId", new Resolver() { public Object get() { return NmsAPI.getEnchantIDProvider().get(); } });
        map.put("entityMethods", new Resolver() { public Object get() { return NmsAPI.getEntityMethods(); } });
        map.put("itemEditor", new Resolver() { public Object get() { return NmsAPI.getItemEditor(); } });
        // Deliberately absent above 1.16.5: the provider is named Pre_1_17 and throws there. It is
        // listed so the report says so, rather than leaving a reader to wonder why it is missing.
        map.put("itemText (pre-1.17 only)", new Resolver() { public Object get() { return NmsAPI.getItemText(); } });
        map.put("mainHand", new Resolver() { public Object get() { return NmsAPI.getMainHandProvider().get(); } });
        map.put("messageManager", new Resolver() { public Object get() { return NmsAPI.getMessageManager(); } });
        map.put("nmsItemMethods", new Resolver() { public Object get() { return NmsAPI.getNmsItemMethods(); } });
        map.put("packetHandler", new Resolver() { public Object get() { return NmsAPI.getPacketHandler(); } });
        map.put("teleporter", new Resolver() { public Object get() { return NmsAPI.getTeleporter(); } });
        map.put("versionedComponent", new Resolver() {
            public Object get() { return NmsAPI.getVersionedComponentSerializer().fromPlainText("x"); }
        });
        map.put("nmsWorld", new Resolver() {
            public Object get() { return NmsAPI.getNmsWorldWrapper().get(Bukkit.getWorlds().get(0)); }
        });
        return map;
    }

    @Override
    public void perform(@NotNull CommandContext context) {
        Logger logger = Bukkit.getLogger();
        String header = "[nmsproviders] " + NmsVersion.getMCVersion()
                + " (" + NmsVersion.getFormattedNmsInteger() + ") on Java "
                + System.getProperty("java.specification.version");
        logger.info(header);

        List<String> failures = new ArrayList<String>();
        int ok = 0;
        int notApplicable = 0;
        for (Map.Entry<String, Resolver> entry : providers().entrySet()) {
            String name = entry.getKey();
            try {
                Object impl = entry.getValue().get();
                logger.info("[nmsproviders] " + name + " -> " + impl.getClass().getName());
                ok++;
            } catch (Throwable t) {
                Throwable root = t;
                while (root.getCause() != null) { root = root.getCause(); }
                String why = root.getClass().getSimpleName() + ": " + root.getMessage();
                // A provider that declares itself unavailable on this version is doing its job.
                // ItemTextProviderPre_1_17 throws above 1.16.5 on purpose. Reporting that as a
                // failure would make this command cry wolf on every modern server, and a check that
                // always shows red is a check nobody reads.
                boolean outOfRange = root instanceof IllegalArgumentException
                        && root.getMessage() != null
                        && root.getMessage().startsWith("Version not supported");
                if (outOfRange) {
                    logger.info("[nmsproviders] " + name + " -> n/a on this version, by design");
                    notApplicable++;
                } else {
                    logger.log(Level.SEVERE, "[nmsproviders] " + name + " -> FAILED " + why, t);
                    failures.add(name + " (" + root.getClass().getSimpleName() + ")");
                }
            }
        }

        int total = providers().size();
        String result = "[nmsproviders] RESULT: " + (failures.isEmpty() ? "RESOLVED" : "FAILED")
                + " " + ok + "/" + (total - notApplicable) + " on " + NmsVersion.getMCVersion()
                + (notApplicable > 0 ? " (" + notApplicable + " n/a on this version)" : "")
                + (failures.isEmpty() ? "" : "; failed: " + String.join(", ", failures));
        logger.log(failures.isEmpty() ? Level.INFO : Level.SEVERE, result);

        NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                (failures.isEmpty() ? "<green>" : "<red>") + "nmsproviders: " + ok + "/"
                        + (total - notApplicable)
                        + " resolved. See console for the implementation each one selected."
        ).sendTo(context.getSender());
    }

    /** A provider lookup. Kept as an interface rather than a lambda: this module targets Java 8. */
    private interface Resolver {
        @NotNull Object get();
    }
}
