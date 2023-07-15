package com.kamikazejamplugins.kamicommon.yaml.handler;

import com.kamikazejamplugins.kamicommon.configuration.config.AbstractConfig;
import com.kamikazejamplugins.kamicommon.util.data.ANSI;

import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public class YamlHandlerStandalone extends AbstractYamlHandler {

    public YamlHandlerStandalone(AbstractConfig abstractConfig, File configFile) {
        super(abstractConfig, configFile);
    }

    public YamlHandlerStandalone(AbstractConfig abstractConfig, File configFile, String fileName) {
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
}
