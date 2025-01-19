package com.kamikazejam.kamicommon.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter @Setter
public class CommandContext {
    // The raw string arguments passed upon execution. An empty list if there are none.
    private final @NotNull List<String> args;
    private final @NotNull CommandSender sender;
    private final @Nullable Player me;
    private final boolean senderIsConsole;

    // The index of the next arg to read.
    private int nextArg = 0;

    public CommandContext(@NotNull List<String> args, @NotNull CommandSender sender) {
        this.args = args;
        this.sender = sender;
        if (sender instanceof Player p) {
            senderIsConsole = false;
            me = p;
        }else {
            senderIsConsole = true;
            me = null;
        }
    }
}
