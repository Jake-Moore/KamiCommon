package com.kamikazejam.kamicommon.modules;

import com.kamikazejam.kamicommon.configuration.config.KamiConfigExt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public class ModuleConfig extends KamiConfigExt {
    private final Module module;

    // Filename is in form: "moduleYmlPath + <module>.yml"
    public ModuleConfig(Module module, String fileName) {
        super(
                // Plugin
                module.getPlugin(),
                // File on server filesystem
                new File(module.getPlugin().getDataFolder() + File.separator + "modules" + File.separator + module.getName() + ".yml"),
                // Supplier for config resource inputstream
                () -> ModuleConfig.getIS(module, fileName)
        );
        this.module = module;
        loadDefaultConfig();
    }

    private static @Nonnull InputStream getIS(Module module, String fileName) {
        InputStream moduleStream = module.getPlugin().getResource(fileName);
        assert moduleStream != null;
        return moduleStream;
    }

    public void loadDefaultConfig() {
        if (module != null) {
            KamiConfigExt c = module.getPlugin().getModulesConfig();
            String name = module.getName().replace(" ", "_");
            c.addDefault("modules." + name + ".enabled", true);
            c.addDefault("modules." + name + ".modulePrefix", module.defaultPrefix());
            c.save();
        }

        this.save();
        this.reload();
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        if (clazz.isInstance(module)) {
            return clazz.cast(module);
        }
        return null;
    }

    public Module getRawModule() {
        return module;
    }

    public Location getLocation(String key) {
        World world = Bukkit.getWorld(getString(key + ".world"));
        if (world == null) {
            String err = "[" + module.getName() + "Config] Invalid world for location: " + key;
            throw new IllegalArgumentException(err);
        }

        double x = getDouble(key + ".x");
        double y = getDouble(key + ".y");
        double z = getDouble(key + ".z");
        float yaw = getFloat(key + ".yaw");
        float pitch = getFloat(key + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
