package com.kamikazejam.kamicommon.integrations.combat;

import nl.marido.deluxecombat.DeluxeCombat;
import nl.marido.deluxecombat.objects.PVPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeluxeCombatImpl implements CombatIntegration {
    public DeluxeCombatImpl() {}

    @Override
    public boolean isTagged(@NotNull Player player) {
        return DeluxeCombat.getAPI().isInCombat(player);
    }

    @Override
    public boolean isTagged(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) { return false; }
        return DeluxeCombat.getAPI().isInCombat(player);
    }

    @Override
    public void tag(@NotNull Player victim, @NotNull Player attacker) {
        PVPPlayer pvpPlayer = DeluxeCombat.getInstance().getSettings().getPlayer(victim);
        pvpPlayer.getTagger().activate(attacker);
    }

    @Override
    public void untag(@NotNull Player player) {
        DeluxeCombat.getAPI().untag(player);
    }

    @Override
    public void untag(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) { return; }
        DeluxeCombat.getAPI().untag(player);
    }
}
