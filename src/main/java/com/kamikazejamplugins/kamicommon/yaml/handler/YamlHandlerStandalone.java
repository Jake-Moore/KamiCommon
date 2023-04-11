package com.kamikazejamplugins.kamicommon.yaml.handler;

import com.kamikazejamplugins.kamicommon.util.data.ANSI;

import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public class YamlHandlerStandalone extends AbstractYamlHandler {

    public YamlHandlerStandalone(File configFile) {
        super(configFile);
    }

    public YamlHandlerStandalone(File configFile, String fileName) {
        super(configFile, fileName);
    }

    @Override
    public InputStream getIS() {
        InputStream i1 = this.getClass().getResourceAsStream("/" + configFile.getName());
        if (i1 != null) { return i1; }

        return YamlHandlerStandalone.class.getClassLoader().getResourceAsStream(File.separator + configFile.getName());
    }

    @Override
    public void error(String s) {
        System.out.println(ANSI.RED + "[KamiCommon] " + s);
    }
}
