package com.kamikazejam.kamicommon.integrations.combat;

import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.PvPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PvpManagerImpl implements CombatIntegration {
    @Override
    public boolean isTagged(Player player) {
        return PvPManager.getInstance().getPlayerHandler().get(player).isInCombat();
    }

    @Override
    public void tag(Player victim, @Nonnull Player attacker) {
        PvPlayer attackerP = PvPManager.getInstance().getPlayerHandler().get(attacker);
        PvPManager.getInstance().getPlayerHandler().get(victim).setTagged(true, attackerP);
    }
}
