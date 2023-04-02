package com.kamikazejamplugins.kamicommon.command.impl;

import com.kamikazejamplugins.kamicommon.VersionControl;
import com.kamikazejamplugins.kamicommon.command.KamiSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class KamiVerCmd extends KamiSubCommand {
    private final JavaPlugin plugin;

    public KamiVerCmd(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> getNames() { return Arrays.asList("ver", "version"); }

    @Override
    public boolean requiresPlayer() { return false; }

    @Override
    public boolean hasPermission(CommandSender sender) { return false; }

    @Override
    public boolean requiresAdmin() { return true; }

    @Override
    public boolean performCommand(CommandSender sender, String label, String[] args) {
        VersionControl.sendDetails(plugin, sender);
        return true;
    }
}
