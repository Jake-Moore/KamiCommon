package com.kamikazejam.kamicommon.util.interfaces;

import com.kamikazejam.kamicommon.KamiPlugin;

@SuppressWarnings("unused")
public interface Active {
    // Boolean
    boolean isActive();

    void setActive(boolean active);

    // Plugin
    void setActivePlugin(KamiPlugin activePlugin);

    KamiPlugin getActivePlugin();

    // Combined Setter
    // Plugin is set first.
    // Boolean by null state.
    void setActive(KamiPlugin plugin);
}
