import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.configuration.config.StandaloneConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Test {
    public static void main(String[] args) {
        String defs = "C:\\Users\\Jake\\Desktop\\Spigot Plugins\\KamiCommon\\src\\test\\java\\test.yml";
        String path = "C:\\Users\\Jake\\Desktop\\Spigot Plugins\\KamiCommon\\src\\test\\java\\out" + "put.yml";

        // Delete Output for Consistency in runs
        File f = new File(path);
        if (f.exists()) {
            if (!f.delete()) { System.out.println("Failed to delete file: " + f.getAbsolutePath()); }
        }

        long ms = System.currentTimeMillis();
        KamiCommon.getYaml();
        System.out.println("Initial Yaml Took: " + (System.currentTimeMillis() - ms) + " ms.\n");

//        ms = System.currentTimeMillis();
//        System.out.println("Starting C1");
//        StandaloneConfig config1 = new StandaloneConfig(new File(path), false, null);
//        System.out.println("C1 Took: " + (System.currentTimeMillis() - ms) + " ms.");
//        System.out.println(" ");

        ms = System.currentTimeMillis();
        new StandaloneConfig(new File(path), () -> {
            try {
                return new FileInputStream(defs);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("\nC2 Took: " + (System.currentTimeMillis() - ms) + " ms.");
    }
}
