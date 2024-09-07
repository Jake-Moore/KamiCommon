package com.kamikazejam.kamicommon.integrations.combat;

import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CombatTagPlusImpl implements CombatIntegration {
    private final CombatTagPlus combatTagPlus;
    public CombatTagPlusImpl() {
        combatTagPlus = (CombatTagPlus) Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus");
    }

    @Override
    public boolean isTagged(@NotNull Player player) {
        return combatTagPlus.getTagManager().isTagged(player.getUniqueId());
    }

    @Override
    public boolean isTagged(@NotNull UUID uuid) {
        return combatTagPlus.getTagManager().isTagged(uuid);
    }

    @Override
    public void tag(@NotNull Player victim, @NotNull Player attacker) {
        combatTagPlus.getTagManager().tag(victim, attacker);
    }

    @Override
    public void untag(@NotNull Player player) {
        combatTagPlus.getTagManager().untag(player.getUniqueId());
    }

    @Override
    public void untag(@NotNull UUID uuid) {
        combatTagPlus.getTagManager().untag(uuid);
    }
}
