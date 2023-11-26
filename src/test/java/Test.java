import com.kamikazejam.kamicommon.configuration.config.StandaloneConfig;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class Test {
    public static void main(String[] args) throws IOException {
        String defs = "C:\\Users\\Jake\\Desktop\\Spigot Plugins\\KamiCommon\\src\\test\\java\\test-big.yml";
        String path = "C:\\Users\\Jake\\Desktop\\Spigot Plugins\\KamiCommon\\src\\test\\java\\output.yml";

        long ymlMs = System.currentTimeMillis();
        YamlUtil.getYaml();
        System.out.println("Initial Yaml Took: " + (System.currentTimeMillis() - ymlMs) + " ms.");

        // Delete Files
        File f = new File(path);
        if (f.exists()) {
            if (!f.delete()) { System.out.println("Failed to delete file: " + f.getAbsolutePath()); }
        }

        long ms = System.currentTimeMillis();
        new StandaloneConfig(new File(path), () -> {
            try {
                return new FileInputStream(defs);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        File d = new File(defs);
        int defLines = IOUtils.readLines(Files.newInputStream(d.toPath())).size();
        int cfgLines = IOUtils.readLines(Files.newInputStream(f.toPath())).size();
        System.out.println("Took: " + (System.currentTimeMillis() - ms) + " ms to load " + cfgLines + "/" + defLines + " lines.");
    }
}
