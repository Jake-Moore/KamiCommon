package com.kamikazejam.kamicommon.integrations.combat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public interface CombatIntegration {

    boolean isTagged(@NotNull Player player);
    boolean isTagged(@NotNull UUID uuid);

    void tag(@NotNull Player victim, @NotNull Player attacker);

    void untag(@NotNull Player player);
    void untag(@NotNull UUID uuid);

}
