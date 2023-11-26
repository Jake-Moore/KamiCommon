package com.kamikazejam.kamicommon.yaml.standalone;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public class YamlUtil {
    private static Yaml yaml = null;
    public static @NotNull Yaml getYaml() {
        if (yaml == null) {
            // Configure LoaderOptions
            LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setProcessComments(true);

            // Configure DumperOptions
            DumperOptions dumperOptions = getDumperOptions();

            // Create a Yaml object with our loading and dumping options
            yaml = (new Yaml(new Constructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions));
        }
        return yaml;
    }

    @NotNull
    private static DumperOptions getDumperOptions() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setAllowUnicode(true);
        dumperOptions.setProcessComments(true);
        dumperOptions.setPrettyFlow(false); // When Disabled, [] will be used for empty lists instead of [\n]  (Keep Disabled)
        dumperOptions.setSplitLines(false); // When Enabled, string lines might be split into multiple lines   (Keep Disabled)
        return dumperOptions;
    }
}
