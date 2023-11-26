import com.kamikazejam.kamicommon.configuration.config.StandaloneConfig;
import com.kamikazejam.kamicommon.util.StringUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Test2 {
    public static void main(String[] args) throws InvalidConfigurationException {
        File defs = new File("C:\\Users\\Jake\\Desktop\\Spigot Plugins\\KamiCommon\\src\\test\\java\\test2.yml");
        StandaloneConfig config = new StandaloneConfig(new File("C:\\Users\\Jake\\Desktop\\test2.yml"), () -> {
            try {
                return new FileInputStream(defs);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        config.save();


        System.out.println("**********");
        System.out.println(config.getString("items.0"));
        System.out.println("**********");
    }
}
