package com.kamikazejam.kamicommon.modules.commands;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.modules.Module;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This command is used to log unmatched arguments. <br>
 * There is a method {@link #sendSubCommandsMap(Map)} that is called when the subCommandsMap should be logged. <br>
 * You can forward this information wherever you want. <br>
 */
@SuppressWarnings("unused")
public abstract class UnmatchedCommand extends KamiCommand implements Disableable {
    private final Map<String, Integer> subCommandMap = new HashMap<>();

    public UnmatchedCommand(KamiPlugin plugin) {
        plugin.registerDisableables(this);
    }
    public UnmatchedCommand(Module module) {
        module.registerDisableables(this);
    }

    @Override
    public void onUnmatchedArg(@NotNull CommandContext context) {
        if (context.getArgs().isEmpty()) { return; }
        String arg1 = context.getArgs().getFirst().toLowerCase();

        int count = subCommandMap.getOrDefault(arg1, 0);
        subCommandMap.put(arg1, count + 1);
        if (subCommandMap.size() >= 50) {
            sendSubCommandsMap(subCommandMap);
            subCommandMap.clear();
        }
    }

    @Override
    public void onDisable() {
        sendSubCommandsMap(subCommandMap);
        subCommandMap.clear();
    }

    /**
     * Called when the subCommandsMap should be logged/
     * @param subCommandsMap The subCommandsMap (Key: SubCommand, Value: Times Attempted)
     */
    public abstract void sendSubCommandsMap(Map<String, Integer> subCommandsMap);
}
