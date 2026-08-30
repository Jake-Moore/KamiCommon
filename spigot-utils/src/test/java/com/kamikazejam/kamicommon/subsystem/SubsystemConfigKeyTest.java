package com.kamikazejam.kamicommon.subsystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the fact that exactly one place builds a subsystem's plugin-level config key.
 * <p>
 * There used to be three spellings of it. {@code ModuleConfig} mapped spaces to underscores for the
 * {@code enabled} flag, {@code Module#getPrefix} used the raw name for {@code modulePrefix}, and
 * {@code Feature#getPrefix} used the raw name for {@code featurePrefix}. A subsystem whose name
 * contained a space therefore read differently-spelled keys off one name. They all go through
 * {@link AbstractSubsystem#getSubsystemConfigKey()} now.
 * <p>
 * <b>Why this reads class files instead of calling the methods.</b> Bukkit is not on this module's
 * test classpath, so {@code AbstractSubsystem} and everything under it cannot be loaded at all:
 * {@code Module} fails with {@code NoClassDefFoundError: org/bukkit/plugin/Plugin}. Reading the
 * bytes off the classpath needs no class loading and no server, and the invariant worth protecting
 * is structural anyway. This checks the compiled output, which is what ships, rather than the
 * source.
 */
class SubsystemConfigKeyTest {

    private static byte[] classBytes(String binaryName) {
        String resource = binaryName.replace('.', '/') + ".class";
        try (InputStream in = SubsystemConfigKeyTest.class.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(in, "could not find " + resource + " on the test classpath");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) { out.write(buf, 0, n); }
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("could not read " + resource, e);
        }
    }

    /**
     * Whether the class file contains this text. Constant pool entries are modified UTF-8, which is
     * plain ASCII for everything asserted here, so the bytes appear literally.
     */
    private static boolean contains(String binaryName, String text) {
        return new String(classBytes(binaryName), StandardCharsets.ISO_8859_1).contains(text);
    }

    private static final String ABSTRACT = "com.kamikazejam.kamicommon.subsystem.AbstractSubsystem";
    private static final String MODULE = "com.kamikazejam.kamicommon.subsystem.module.Module";
    private static final String MODULE_CONFIG = "com.kamikazejam.kamicommon.subsystem.module.ModuleConfig";
    private static final String FEATURE = "com.kamikazejam.kamicommon.subsystem.feature.Feature";
    private static final String FEATURE_CONFIG = "com.kamikazejam.kamicommon.subsystem.feature.FeatureConfig";

    @Test
    @DisplayName("no subsystem class builds a config key of its own")
    void nobodyBuildsTheirOwnKey() {
        // "modules." and "features." with the dot is what a hand-built key looks like. The bare
        // section names are fine: those are the two getSubsystemConfigSection() overrides.
        for (String type : new String[]{ABSTRACT, MODULE, MODULE_CONFIG, FEATURE, FEATURE_CONFIG}) {
            assertFalse(contains(type, "modules."),
                    type + " contains the literal \"modules.\", so it builds a key itself instead of "
                            + "calling AbstractSubsystem#getSubsystemConfigKey()");
            assertFalse(contains(type, "features."),
                    type + " contains the literal \"features.\", so it builds a key itself instead of "
                            + "calling AbstractSubsystem#getSubsystemConfigKey()");
        }
    }

    @Test
    @DisplayName("both Module and Feature reach the shared key builder")
    void bothReachTheSharedBuilder() {
        assertTrue(contains(MODULE, "getSubsystemConfigKey"),
                "Module does not reference getSubsystemConfigKey");
        assertTrue(contains(FEATURE, "getSubsystemConfigKey"),
                "Feature does not reference getSubsystemConfigKey");
        assertTrue(contains(MODULE_CONFIG, "getModulesConfigKey"),
                "ModuleConfig does not reference getModulesConfigKey, which is the only caller "
                        + "keeping that public alias honest");
    }

    @Test
    @DisplayName("the space-to-underscore normalisation lives in AbstractSubsystem alone")
    void normalisationLivesInOnePlace() {
        assertTrue(contains(ABSTRACT, "getSubsystemConfigKey"),
                "AbstractSubsystem does not declare getSubsystemConfigKey");
        // Module and Feature must not carry the replace(" ", "_") call themselves.
        for (String type : new String[]{MODULE, FEATURE, MODULE_CONFIG, FEATURE_CONFIG}) {
            assertFalse(contains(type, "replace"),
                    type + " calls String.replace, which is how the old duplicated normalisation "
                            + "looked. Normalising belongs to AbstractSubsystem#getSubsystemConfigKey()");
        }
    }
}
