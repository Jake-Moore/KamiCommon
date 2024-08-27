package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.configuration.config.AbstractConfig;
import com.kamikazejam.kamicommon.util.data.ANSI;
import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public class YamlHandlerStandalone extends AbstractYamlHandler<YamlConfigurationStandalone> {

    public YamlHandlerStandalone(AbstractConfig<?> abstractConfig, File configFile) {
        super(abstractConfig, configFile);
    }

    public YamlHandlerStandalone(AbstractConfig<?> abstractConfig, File configFile, String fileName) {
        super(abstractConfig, configFile, fileName);
    }

    @Override
    public InputStream getIS() {
        InputStream i1 = this.getClass().getResourceAsStream("/" + configFile.getName());
        if (i1 != null) { return i1; }

        InputStream i2 = this.getClass().getClassLoader().getResourceAsStream("/" + configFile.getName());
        if (i2 != null) { return i2; }

        return YamlHandlerStandalone.class.getClassLoader().getResourceAsStream(File.separator + configFile.getName());
    }

    @Override
    public void error(String s) {
        System.out.println(ANSI.RED + "[KamiCommon] " + s);
    }


    @Override
    public YamlConfigurationStandalone newConfig(MappingNode node, File configFile) {
        return new YamlConfigurationStandalone(node, configFile);
    }

    @Override
    public MemorySectionMethods<?> newMemorySection(MappingNode node) {
        return new MemorySectionStandalone(node, "");
    }
}
