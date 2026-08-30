package com.kamikazejam.kamicommon.integrations;

import com.kamikazejam.kamicommon.integrations.combat.CombatIntegration;
import com.kamikazejam.kamicommon.integrations.combat.CombatTagPlusImpl;
import com.kamikazejam.kamicommon.integrations.combat.DeluxeCombatImpl;
import com.kamikazejam.kamicommon.integrations.combat.PvpManagerImpl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves whichever supported combat-tag plugin is installed to a single {@link CombatIntegration}, trying
 * CombatTagPlus, then DeluxeCombat, then PvPManager. {@link #get()} returns {@code null} when none of them
 * is present, and the lookup runs once and is then cached either way, so calling it before the server has
 * enabled its plugins caches the absence for the rest of the run.
 */
@SuppressWarnings("unused")
public class CombatSource {
    private static @Nullable CombatIntegration integration;
    private static boolean loaded = false;
    public static @Nullable CombatIntegration get() {
        if (!loaded && integration == null) {
            if (Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus") != null) {
                integration = new CombatTagPlusImpl();
            }else if (Bukkit.getServer().getPluginManager().getPlugin("DeluxeCombat") != null) {
                integration = new DeluxeCombatImpl();
            }else if (Bukkit.getServer().getPluginManager().getPlugin("PvPManager") != null) {
                integration = new PvpManagerImpl();
            }
        }

        loaded = true;
        return integration;
    }
}
