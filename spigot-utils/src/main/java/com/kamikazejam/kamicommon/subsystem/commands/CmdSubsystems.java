package com.kamikazejam.kamicommon.subsystem.commands;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.command.util.CommandPaging;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public class CmdSubsystems extends KamiCommand {
    private final @NotNull KamiPlugin plugin;
    private final @NotNull Function<KamiPlugin, List<AbstractSubsystem<?,?>>> supplier;
    private final @NotNull String paginationTitle;
    public CmdSubsystems(
            @NotNull KamiPlugin plugin,
            @Nullable String permission,
            @NotNull String desc,
            @NotNull Function<KamiPlugin, List<AbstractSubsystem<?,?>>> supplier,
            @NotNull String paginationTitle,
            @NotNull String... aliases
    ) {
        this.plugin = plugin;
        this.supplier = supplier;
        this.paginationTitle = paginationTitle;
        addAliases(aliases);

        if (permission != null) {
            addRequirements(RequirementHasPerm.get(permission));
        }

        // Add page param
        this.addParameter(Parameter.of(TypeInteger.get()).name("page").defaultValue(1));

        setDesc(desc);
    }

    @Override
    public void perform(@NotNull CommandContext context) throws KamiCommonException {
        int page = readArg();
        CommandSender sender = context.getSender();

        // Loop through subsystems in alphabetical order
        List<AbstractSubsystem<?,?>> subsystems = new ArrayList<>(supplier.apply(this.plugin));
        subsystems.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        // Create each line of the subsystem list
        List<KMessageSingle> lines = new ArrayList<>();
        int size = subsystems.size();
        for (int i = 0; i < size; i++) {
            AbstractSubsystem<?,?> subsystem = subsystems.get(i);
            int pos = i + 1;
            String status = subsystem.isEnabled() ? (subsystem.isSuccessfullyEnabled() ? "&aENABLED" : "&cERROR") : "&6DISABLED";
            String content = " &7- &f" + getPaddedNumber(pos, size) + ". " + subsystem.getName() + " &7- " + status;
            lines.add(new KMessageSingle(LegacyColors.t(content)));
        }

        // Create the Pagination of Each Subsystem Line
        List<KMessage> messages = CommandPaging.getPage(this, lines, page, paginationTitle);
        NmsAPI.getMessageManager().processAndSend(sender, messages);
    }

    private @NotNull String getPaddedNumber(int value, int maxValue) {
        // Count how many characters the max value has, so we can pad the current value accordingly
        int maxChars = String.valueOf(maxValue).length();
        String strValue = String.valueOf(value);
        // Pad with spaces in front to match the length of maxChars
        return " ".repeat(Math.max(0, maxChars - strValue.length())) + strValue;
    }
}
