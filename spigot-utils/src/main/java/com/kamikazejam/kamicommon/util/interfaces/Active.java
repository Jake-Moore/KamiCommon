package com.kamikazejam.kamicommon.util.interfaces;

import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public interface Active {
    // Boolean
    boolean isActive();

    void setActive(boolean active);

    // Plugin
    void setActivePlugin(Plugin plugin);

    Plugin getActivePlugin();

    // Combined Setter
    // Plugin is set first.
    // Boolean by null state.
    void setActive(Plugin plugin);
}
