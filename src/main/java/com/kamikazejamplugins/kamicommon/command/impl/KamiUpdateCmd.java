package com.kamikazejamplugins.kamicommon.command.impl;

import com.kamikazejamplugins.kamicommon.autoupdate.AutoUpdate;
import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.command.KamiSubCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class KamiUpdateCmd extends KamiSubCommand {

    @Override
    public List<String> getNames() { return Collections.singletonList("update"); }

    @Override
    public boolean requiresPlayer() { return false; }

    @Override
    public boolean hasPermission(CommandSender sender) { return false; }

    @Override
    public boolean requiresAdmin() { return true; }

    @Override
    public boolean performCommand(CommandSender sender, String label, String[] args) {
        AutoUpdate.updateNow(KamiCommon.getPlugin());
        return true;
    }
}
