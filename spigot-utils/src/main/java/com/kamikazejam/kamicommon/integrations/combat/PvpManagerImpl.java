package com.kamikazejam.kamicommon.integrations.combat;

import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.PvPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PvpManagerImpl implements CombatIntegration {
    @Override
    public boolean isTagged(@NotNull Player player) {
        return PvPManager.getInstance().getPlayerHandler().get(player).isInCombat();
    }

    @Override
    public boolean isTagged(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) { return false; }
        return PvPManager.getInstance().getPlayerHandler().get(player).isInCombat();
    }

    @Override
    public void tag(@NotNull Player victim, @Nonnull Player attacker) {
        PvPlayer attackerP = PvPManager.getInstance().getPlayerHandler().get(attacker);
        PvPManager.getInstance().getPlayerHandler().get(victim).setTagged(true, attackerP);
    }

    @Override
    public void untag(@NotNull Player player) {
        PvPManager.getInstance().getPlayerHandler().get(player).unTag();
    }

    @Override
    public void untag(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) { return; }
        PvPManager.getInstance().getPlayerHandler().get(player).unTag();
    }
}
