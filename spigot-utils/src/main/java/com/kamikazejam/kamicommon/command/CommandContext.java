package com.kamikazejam.kamicommon.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The state of a single command invocation, handed to {@link KamiCommand#perform(CommandContext)}. It carries
 * the sender, the alias actually typed, the raw arguments, and the read cursor that
 * {@link KamiCommand#readArg()} advances. The framework builds one per execution and clears it once
 * execution ends, so it is valid only inside {@code perform} and must not be retained or read from another
 * thread.
 *
 * @see <a href="https://github.com/Jake-Moore/KamiCommon/wiki/v5-KamiCommand#commandcontext">CommandContext (wiki)</a>
 */
@Getter @Setter
public class CommandContext {
    // The raw string arguments passed upon execution. An empty list if there are none.
    private final @NotNull String label; // The current command's 'label' i.e. the alias used to invoke it.
    private final @NotNull List<String> args;
    private final @NotNull CommandSender sender;
    private final @Nullable Player me;
    private final boolean senderIsConsole;

    // The index of the next arg to read.
    private int nextArg = 0;

    public CommandContext(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        this.label = label;
        this.args = args;
        this.sender = sender;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            senderIsConsole = false;
            me = p;
        }else {
            senderIsConsole = true;
            me = null;
        }
    }
}
