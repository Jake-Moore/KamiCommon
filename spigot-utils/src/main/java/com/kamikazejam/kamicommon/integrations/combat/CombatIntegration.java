package com.kamikazejam.kamicommon.integrations.combat;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface CombatIntegration {

    boolean isTagged(Player player);

    void tag(Player victim, @Nonnull Player attacker);

}
