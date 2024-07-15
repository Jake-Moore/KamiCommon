package com.kamikazejam.kamicommon.integrations.combat;

import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CombatTagPlusImpl implements CombatIntegration {
    private final CombatTagPlus combatTagPlus;
    public CombatTagPlusImpl() {
        combatTagPlus = (CombatTagPlus) Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus");
    }

    @Override
    public boolean isTagged(Player player) {
        return combatTagPlus.getTagManager().isTagged(player.getUniqueId());
    }

    @Override
    public void tag(Player victim, @Nonnull Player attacker) {
        combatTagPlus.getTagManager().tag(victim, attacker);
    }
}
