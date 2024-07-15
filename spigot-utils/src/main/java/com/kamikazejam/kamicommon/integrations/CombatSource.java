package com.kamikazejam.kamicommon.integrations;

import com.kamikazejam.kamicommon.integrations.combat.CombatIntegration;
import com.kamikazejam.kamicommon.integrations.combat.CombatTagPlusImpl;
import com.kamikazejam.kamicommon.integrations.combat.DeluxeCombatImpl;
import com.kamikazejam.kamicommon.integrations.combat.PvpManagerImpl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

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
