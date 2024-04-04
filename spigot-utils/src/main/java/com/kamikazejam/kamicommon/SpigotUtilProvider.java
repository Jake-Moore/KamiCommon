package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.command.type.RegistryType;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotUtilProvider {
    private static KamiPlugin plugin = null;
    public static @NotNull KamiPlugin getPlugin() {
        Preconditions.checkNotNull(plugin, "SpigotUtilPluginProvider was not initialized!!!");
        return plugin;
    }
    public static void setPlugin(@NotNull KamiPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        SpigotUtilProvider.plugin = plugin;

        // Setup RegistryType (Types for Commands)
        RegistryType.registerAll();
        // Setup Commands
        new KamiCommonCommandRegistration(plugin);
    }

    @Setter private static @Nullable PremiumVanishIntegration vanishIntegration = null;
    private static Boolean vanishAPI = null;
    public static @Nullable PremiumVanishIntegration getVanishIntegration() {
        if (vanishAPI == null) {
            vanishAPI = Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish");
        }
        if (vanishAPI && vanishIntegration == null) {
            vanishIntegration = new PremiumVanishIntegration(getPlugin());
        }
        return vanishIntegration;
    }
}
