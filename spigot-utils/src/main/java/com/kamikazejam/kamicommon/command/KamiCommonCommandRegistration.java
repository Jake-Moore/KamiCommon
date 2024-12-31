package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KamiCommonCommandRegistration implements Listener {
    private static @Nullable KamiCommonCommandRegistration i = null;
    public static KamiCommonCommandRegistration get(@NotNull KamiPlugin plugin) {
        if (i == null) { i = new KamiCommonCommandRegistration(plugin); }
        return i;
    }

    private static boolean serverStarted = false;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //
    private KamiCommonCommandRegistration(@NotNull KamiPlugin plugin) {
        // Step 1 - Register a task to run 1 tick after server start, which will register the commands.
        // And mark the server as started, so all future calls to updateRegistrationsInternal() will be executed immediately.
        new BukkitRunnable() {
            @Override
            public void run() {
                serverStarted = true;
                updateRegistrations();
            }
        }.runTaskLater(plugin, 1L); // 1 tick after boot

        // Step 2 - Register a period task to run every 5 minutes to update the registrations.
        // This is done to ensure that any plugin forgetting to call the update method will still have their commands registered. (eventually)
        plugin.registerTasks(new BukkitRunnable() {
            @Override
            public void run() {
                updateRegistrations();
            }
        }.runTaskTimer(plugin, 20L * 10L, 20L * 60L * 15L)); // 10 seconds after boot, then every 15m
    }

    // -------------------------------------------- //
    // UPDATE REGISTRATIONS
    // -------------------------------------------- //
    public static void updateRegistrations() {
        // During server startup (serverStarted = false)
        if (!serverStarted) {
            // Skip execution during startup - our 1 tick delay task will handle registration and updating this boolean after server start.
            return;
        }

        // Server has started, execute immediately
        updateRegistrationsInternal();
    }

    private static void updateRegistrationsInternal() {
        // Step #1: Hack into Bukkit and get the SimpleCommandMap and it's knownCommands.
        SimpleCommandMap simpleCommandMap = getSimpleCommandMap();
        Map<String, Command> knownCommands = getSimpleCommandMapDotKnownCommands(simpleCommandMap);

        // Step #2: Create a "name --> target" map that contains the KamiCommands that /should/ be registered in Bukkit. 
        Map<String, KamiCommand> nameTargets = new HashMap<>();
        // For each KamiCommand that is supposed to be registered ...
        for (KamiCommand kamiCommand : KamiCommand.getAllInstances()) {
            // ... and for each of it's aliases ...
            for (String alias : kamiCommand.getAliases()) {
                // ... that aren't null ...
                if (alias == null) continue;

                // ... clean the alias ...
                alias = alias.trim().toLowerCase();

                // ... and put it in the map.
                // NOTE: In case the same alias is used by many commands the overwrite occurs here!
                nameTargets.put(alias, kamiCommand);
            }
        }

        // Step #3: Ensure the nameTargets created in Step #2 are registered in Bukkit.
        // For each nameTarget entry ...
        for (Map.Entry<String, KamiCommand> entry : nameTargets.entrySet()) {
            String name = entry.getKey();
            KamiCommand target = entry.getValue();

            // ... find the current command registered in Bukkit under that name (if any) ...
            Command current = knownCommands.get(name);
            KamiCommand kamiCurrent = getKamiCommand(current);

            // ... and if the current command is not the target ...
            // NOTE: We do this check since it's important we don't create new KamiCommonBukkitCommand unless required.
            // NOTE: Before I implemented this check I caused a memory leak in tandem with Spigots timings system.
            if (target == kamiCurrent) continue;

            // ... unregister the current command if there is one ...
            if (current != null) {
                knownCommands.remove(name);
                current.unregister(simpleCommandMap);
            }

            // ... create a new KamiCommonBukkitCommand ...
            KamiCommonBukkitCommand command = new KamiCommonBukkitCommand(name, target);

            // ... and finally register it.
            Plugin plugin = command.getPlugin();
            String pluginName = plugin.getName();
            simpleCommandMap.register(pluginName, command);
        }

        // Step #4: Remove/Unregister KamiCommands from Bukkit that are but should not be that any longer. 
        // For each known command ...
        Iterator<Map.Entry<String, Command>> iter = knownCommands.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Command> entry = iter.next();
            String name = entry.getKey();
            Command command = entry.getValue();

            // ... that is a KamiCommonBukkitCommand ...
            KamiCommand kamiCommand = getKamiCommand(command);
            if (kamiCommand == null) continue;

            // ... and not a target ...
            if (nameTargets.containsKey(name)) continue;

            // ... unregister it.
            command.unregister(simpleCommandMap);
            iter.remove();
        }
        syncCommands();
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    protected static Field SERVER_DOT_COMMAND_MAP = ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap");

    public static SimpleCommandMap getSimpleCommandMap() {
        Server server = Bukkit.getServer();
        return ReflectionUtil.getField(SERVER_DOT_COMMAND_MAP, server);
    }

    protected static Field SIMPLE_COMMAND_MAP_DOT_KNOWN_COMMANDS = ReflectionUtil.getField(SimpleCommandMap.class, "knownCommands");

    public static Map<String, Command> getSimpleCommandMapDotKnownCommands(SimpleCommandMap simpleCommandMap) {
        return ReflectionUtil.getField(SIMPLE_COMMAND_MAP_DOT_KNOWN_COMMANDS, simpleCommandMap);
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    @Contract("null -> null")
    public static KamiCommand getKamiCommand(Command command) {
        if (command == null) return null;
        if (!(command instanceof KamiCommonBukkitCommand)) return null;
        return ((KamiCommonBukkitCommand) command).getKamiCommand();
    }

    // -------------------------------------------- //
    // 1.13 SYNC COMMANDS
    // -------------------------------------------- //

    private static Method CRAFTSERVER_SYNC_COMMANDS = null;
    private static boolean syncCommandsFailed = false;

    private static Method getSyncCommandMethod() {
        if (CRAFTSERVER_SYNC_COMMANDS != null || syncCommandsFailed) return CRAFTSERVER_SYNC_COMMANDS;
        Class<?> clazz = Bukkit.getServer().getClass();
        try {
            CRAFTSERVER_SYNC_COMMANDS = ReflectionUtil.getMethod(clazz, "syncCommands");
        } catch (Exception ex) {
            syncCommandsFailed = true;
        }
        return CRAFTSERVER_SYNC_COMMANDS;
    }

    private static void syncCommands() {
        Method sync = getSyncCommandMethod();
        if (sync == null) return;
        ReflectionUtil.invokeMethod(sync, Bukkit.getServer());
    }

}
