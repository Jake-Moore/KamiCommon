package com.kamikazejam.kamicommon.subsystem.module;

import com.kamikazejam.kamicommon.KamiPlugin;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ModuleManager {
    private final Map<Class<? extends Module>, Module> moduleMap = new HashMap<>();
    @Getter private final List<Module> moduleList = new ArrayList<>();

    private final KamiPlugin plugin;
    public ModuleManager(KamiPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerModule(Module module) {
        try {
            if (!moduleList.contains(module)) {
                moduleList.add(module);
            }
            moduleMap.put(module.getClass(), module);

            // The call to isEnabledInConfig will handle config state appropriately
            if (module.isEnabledInConfig()) {
                // Enable the module since we want it to be enabled by default
                module.handleEnable();
            }

        } catch (Throwable e) {
            plugin.getLogger().warning("Can not register the module: " + module.getName());
            e.printStackTrace();
        }
    }

    public void unregister() {
        // Disable will remove the feature from the list and map
        // Iterate over a copy to avoid ConcurrentModificationException
        for (Module module : new ArrayList<>(moduleList)) {
            disable(module);
        }
        // Ensure the module structures are cleared
        moduleList.clear();
        moduleMap.clear();
    }

    public boolean disable(Module module) {
        // only disable enabled modules
        if (!module.isSuccessfullyEnabled() || !module.isEnabled()) { return false; }

        try {
            module.handleDisable();
            moduleMap.remove(module.getClass());
            moduleList.remove(module);
            return true;
        } catch (Throwable e) {
            plugin.getLogger().warning("Can not disable the module: " + module.getName());
            e.printStackTrace();
        }
        return false;
    }

    public boolean enable(Module module) {
        // only disable enabled modules
        if (module.isEnabled()) { return false; }

        try {
            registerModule(module);
            return true;
        } catch (Throwable e) {
            plugin.getLogger().warning("Can not enable the module: " + module.getName());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the origin module class by the class name.
     *
     * @param clazz Module class
     * @param <T>   Module
     * @return Origin module class, if not exist null.
     */
    public <T extends Module> T get(Class<T> clazz) {
        Module module = moduleMap.get(clazz);
        if (module == null) {
            for (Module modules : moduleList) {
                if (clazz.isInstance(modules)) {
                    return clazz.cast(modules);
                }
            }
        }
        if (clazz.isInstance(module)) {
            return clazz.cast(module);
        }
        return null;
    }

    @Nullable
    public Module getModuleByName(String name) {
        for (Module module : moduleList) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public final void onItemsAdderLoaded() {
        for (Module module : moduleList) {
            if (!module.isEnabled()) { continue; }
            module.onItemsAdderLoaded();
        }
    }

    public final void onMythicMobsLoaded() {
        for (Module module : moduleList) {
            if (!module.isEnabled()) { continue; }
            module.onMythicMobsLoaded();
        }
    }

    public final void onCitizensLoaded() {
        for (Module module : moduleList) {
            if (!module.isEnabled()) { continue; }
            module.onCitizensLoaded();
        }
    }
}
