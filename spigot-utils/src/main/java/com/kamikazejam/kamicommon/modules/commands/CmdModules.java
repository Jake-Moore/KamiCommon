package com.kamikazejam.kamicommon.modules.commands;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.modules.Module;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CmdModules extends KamiCommand {
    private final KamiPlugin plugin;
    public CmdModules(KamiPlugin plugin, String permission) {
        this.plugin = plugin;
        addAliases("modules");

        addRequirements(RequirementHasPerm.get(permission));

        setDesc("List all modules");
    }

    @Override
    public void perform(@NotNull CommandContext context) throws KamiCommonException {
        CommandSender sender = context.getSender();
        sender.sendMessage(" ");
        sender.sendMessage(StringUtil.t("&eModules:"));
        // Loop through moduleList in alphabetical order
        List<Module> moduleList = plugin.getModuleManager().getModuleList();

        List<Module> modules = new ArrayList<>(moduleList);
        modules.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        for (Module module : modules) {
            int p = moduleList.indexOf(module) + 1;
            String pos = (p < 10 ? " " + p : String.valueOf(p));

            if (module.isEnabled() && module.isSuccessfullyEnabled()) {
                sender.sendMessage(StringUtil.t("  &e" + pos + ". " + module.getName() + " &7- &a" + String.valueOf(module.isEnabled()).toUpperCase()));
            }else if (!module.isSuccessfullyEnabled()) {
                sender.sendMessage(StringUtil.t("  &e" + pos + ". " + module.getName() + " &7- &c" + String.valueOf(module.isEnabled()).toUpperCase()));
            }else {
                sender.sendMessage(StringUtil.t("  &e" + pos + ". " + module.getName() + " &7- &6Disabled"));
            }
        }
    }
}
