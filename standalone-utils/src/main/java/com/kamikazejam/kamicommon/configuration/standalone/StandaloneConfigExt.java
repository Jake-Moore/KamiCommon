package com.kamikazejam.kamicommon.configuration.standalone;

import com.kamikazejam.kamicommon.yaml.standalone.MemorySectionStandalone;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link StandaloneConfig} but with some extended features
 */
@SuppressWarnings("unused")
public class StandaloneConfigExt extends StandaloneConfig {

    public StandaloneConfigExt(File file) {
        super(file);
    }
    public StandaloneConfigExt(File file, boolean addDefaults) {
        super(file, addDefaults);
    }
    public StandaloneConfigExt(File file, Supplier<InputStream> defaultStream) {
        super(file, defaultStream);
    }
    public StandaloneConfigExt(File file, boolean addDefaults, @Nullable Supplier<InputStream> defaultStream) {
        super(file, addDefaults, defaultStream);
    }

    @Override
    public String getString(String key) {
        return this.getString(key, null);
    }
    @Override
    public String getString(String key, String def) {
        String string = super.getString(key, def);
        if (string == null) { return null; }
        return this.applyThisPlaceholders(string);
    }

    @Override
    public List<String> getStringList(String key) {
        return this.getStringList(key, new ArrayList<>());
    }
    @Override
    public List<String> getStringList(String key, List<String> def) {
        List<String> list = super.getStringList(key, def);
        if (list == null) { return null; }
        list.replaceAll(this::applyThisPlaceholders);
        return list;
    }

    public String applyThisPlaceholders(String val) {
        if (val == null) { return null; }
        if (!this.isConfigurationSection("this.placeholders")) { return val; }

        MemorySectionStandalone section = this.getConfigurationSection("this.placeholders");

        // Recursion base case, if there are no placeholders just return the string
        if (!val.contains("{") || !val.contains("}")) { return val; }

        for (String placeholder : section.getKeys(false)) {
            // This is a tradeoff, we can reduce the amount of recursions when fetching placeholders that contain
            //  other placeholders, at the cost of another contains call (worth it imo)
            if (!val.contains("{" + placeholder + "}")) { continue; }

            // Recursion base case, if there are no placeholders just return the string
            val = val.replace("{" + placeholder + "}", section.getString(placeholder));
        }
        return val;
    }
}
