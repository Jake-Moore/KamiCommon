import com.kamikazejam.kamicommon.configuration.standalone.StandaloneConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import com.kamikazejam.kamicommon.yaml.standalone.ConfigurationSectionStandalone;
import com.kamikazejam.kamicommon.yaml.standalone.ConfigurationSequenceStandalone;
import com.kamikazejam.kamicommon.yaml.standalone.MemorySectionStandalone;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Really unofficial, unorganized, incomplete test class for StandaloneConfig and the new Sequence support.
 */
public class TestConfig {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Jake\\Desktop\\Spigot Plugins\\KamiCommon\\standalone-utils\\src\\test\\resources\\testConfig.yml");
        StandaloneConfig config = new StandaloneConfig(new TestConfigLogger(), file, null);

        // Example usage of the config object
        System.out.println("Test 1...");
        String test = config.getString("testConfig.test", null);
        assert test.equals("Hello World!");
        System.out.println("\tTest 1 Passed.");

        // Example of new Sequence usage
        System.out.println("Test 2...");
        ConfigurationSequenceStandalone sequence = config.getConfigurationSequence("testConfig.people");
        System.out.println("\tSequence Size: " + sequence.size());

        System.out.println("\tIteration 1");
        for (int i = 0; i < sequence.size(); i++) {
            ConfigurationSectionStandalone section = sequence.get(i);
            @Nullable String name = section.getString("name", null);
            int age = section.getInt("age", -1);
            if (i == 0) {
                Preconditions.checkArgument("Jake".equals(name), "Name is not Jake");
                Preconditions.checkArgument(age == 23, "Age is not 23");
                System.out.println("\t\tPerson #0: Name = " + name + ", Age = " + age);
            } else if (i == 1) {
                Preconditions.checkArgument("Bob".equals(name), "Name is not Bob");
                Preconditions.checkArgument(age == 25, "Age is not 25");
                System.out.println("\t\tPerson #1: Name = " + name + ", Age = " + age);
            } else if (i == 2) {
                Preconditions.checkArgument("Alice".equals(name), "Name is not Alice");
                Preconditions.checkArgument(age == 22, "Age is not 22");
                System.out.println("\t\tPerson #2: Name = " + name + ", Age = " + age);
            }
        }

        System.out.println("\tIteration 2");
        int i = 0;
        for (ConfigurationSectionStandalone section : sequence) {
            @Nullable String name = section.getString("name", null);
            int age = section.getInt("age", -1);
            if (i == 0) {
                Preconditions.checkArgument("Jake".equals(name), "Name is not Jake");
                Preconditions.checkArgument(age == 23, "Age is not 23");
                System.out.println("\t\tPerson #0: Name = " + name + ", Age = " + age);
            } else if (i == 1) {
                Preconditions.checkArgument("Bob".equals(name), "Name is not Bob");
                Preconditions.checkArgument(age == 25, "Age is not 25");
                System.out.println("\t\tPerson #1: Name = " + name + ", Age = " + age);
            } else if (i == 2) {
                Preconditions.checkArgument("Alice".equals(name), "Name is not Alice");
                Preconditions.checkArgument(age == 22, "Age is not 22");
                System.out.println("\t\tPerson #2: Name = " + name + ", Age = " + age);
            }
            i++;
        }
        System.out.println("\tTest 2 Passed.");

        // Test 2
        System.out.println("Test 3...");
        MemorySectionStandalone nested = config.getConfigurationSection("my.super.nested");
        String superNested = nested.getString("key", null);
        System.out.println("\tSuper Nested Key: " + superNested);
        String newValue = UUID.randomUUID().toString();
        nested.set("key", newValue);

        System.out.println("\tSet New Value '" + newValue + "'");
        boolean test2SectionChanged = nested.isChanged();
        boolean test2ConfigChanged = config.isChanged();
        System.out.println("\tSection Changed?: " + test2SectionChanged);
        System.out.println("\tConfig Changed?: " + test2ConfigChanged);
        Preconditions.checkArgument(test2SectionChanged, "Section Changed");
        Preconditions.checkArgument(test2ConfigChanged, "Config Changed");

//        section.save(file);
        config.save();

        // Test 4
        System.out.println("Test 4...");
        List<String> list = config.getStringList("list", null);
        for (String s : list) {
            System.out.println("\tList Item: " + s);
        }

        // Test 5 - Set everyone's age to 100 if present
        System.out.println("Test 5...");
        ConfigurationSequenceStandalone sequenceNode = config.getConfigurationSequence("testConfig.people");
        for (ConfigurationSectionStandalone section : sequenceNode) {
            if (section.contains("age")) {
                section.set("age", 100);
            }
            section.set("newKey", UUID.randomUUID().toString());
        }
        boolean test5SectionChanged = nested.isChanged();
        boolean test5ConfigChanged = config.isChanged();
        System.out.println("\tSection Changed?: " + test5SectionChanged);
        System.out.println("\tConfig Changed?: " + test5ConfigChanged);
        Preconditions.checkArgument(test5SectionChanged, "Section Changed");
        Preconditions.checkArgument(test5ConfigChanged, "Config Changed");
        config.save();
    }

    private static class TestConfigLogger extends LoggerService {

        @Override
        public String getLoggerName() {
            return "TestConfigLogger";
        }

        @Override
        public boolean isDebug() {
            return true;
        }
    }
}
