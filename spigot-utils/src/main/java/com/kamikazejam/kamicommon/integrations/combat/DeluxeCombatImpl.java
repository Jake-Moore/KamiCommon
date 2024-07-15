package com.kamikazejam.kamicommon.integrations.combat;

import nl.marido.deluxecombat.DeluxeCombat;
import nl.marido.deluxecombat.objects.PVPPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DeluxeCombatImpl implements CombatIntegration {
    public DeluxeCombatImpl() {}

    @Override
    public boolean isTagged(Player player) {
        return DeluxeCombat.getAPI().isInCombat(player);
    }

    @Override
    public void tag(Player victim, @Nonnull Player attacker) {
        PVPPlayer pvpPlayer = DeluxeCombat.getInstance().getSettings().getPlayer(victim);
        pvpPlayer.getTagger().activate(attacker);
    }
}
