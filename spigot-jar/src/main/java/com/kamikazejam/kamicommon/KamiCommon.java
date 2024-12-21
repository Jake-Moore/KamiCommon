package com.kamikazejam.kamicommon;

import lombok.Getter;

/**
 * This class is just the plugin source when the spigot-jar is installed on a server.<br>
 * All registration and enabling of the plugin is handled in {@link PluginSource}.<br>
 * <br>
 * This architecture allows a third party plugin to completely shade the spigot-jar module
 *   and initialize {@link PluginSource} on its own.<br>
 */
@Getter
@SuppressWarnings("unused")
public class KamiCommon extends KamiPlugin {
    private static KamiCommon plugin;

    @Override
    public void onEnableInner() {
        PluginSource.onEnable(this);
    }

    @Override
    public void onDisableInner() {
        PluginSource.onDisable();
    }
}
