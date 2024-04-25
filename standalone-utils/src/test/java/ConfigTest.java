import com.kamikazejam.kamicommon.configuration.config.StandaloneConfigExt;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConfigTest {
    public static void main(String[] args) {
        StandaloneConfigExt config = new StandaloneConfigExt(new File("C:\\Users\\Jake\\Desktop\\config.yml"), false);
        @Nullable String a = config.getString("test.a");
        if (a == null) { a = ""; }

        System.out.println("A: '" + a + "'");

    }
}
