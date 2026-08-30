package com.kamikazejam.kamicommon.menu.api.loaders.menu;

import com.kamikazejam.kamicommon.yaml.spigot.MemorySection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the {@code icons:} guard in {@link SimpleMenuLoader#loadMenu(com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection)}.
 * <p>
 * {@code getConfigurationSection} is {@code @NotNull} and hands back an EMPTY section for a key
 * that is not present, so a menu whose {@code icons:} block was missing, misspelled or mis-indented
 * loaded as a menu with zero icons and opened as an empty inventory, with nothing naming the
 * config. The loader now refuses it and says where.
 * <p>
 * No live server is needed. Everything up to and including the guard is config parsing.
 */
class SimpleMenuLoaderTest {

    private static MemorySection section(String path, String yaml) {
        MappingNode node = (MappingNode) new Yaml().compose(new StringReader(yaml));
        return new MemorySection(node, path, null);
    }

    @Test
    @DisplayName("a menu with no icons block fails, naming the section and the key")
    void missingIconsBlockThrows() {
        MemorySection s = section("menus.shop", "title: '&aShop'\nrows: 3\n");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SimpleMenuLoader.loadMenu(s));
        assertTrue(e.getMessage().contains("menus.shop"), "message must name the section: " + e.getMessage());
        assertTrue(e.getMessage().contains("icons"), "message must name the key: " + e.getMessage());
    }

    @Test
    @DisplayName("an icons key with nothing under it is treated the same as a missing one")
    void emptyIconsBlockThrows() {
        MemorySection s = section("menus.shop", "title: '&aShop'\nrows: 3\nicons:\n");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SimpleMenuLoader.loadMenu(s));
        assertTrue(e.getMessage().contains("menus.shop"), "message must name the section: " + e.getMessage());
    }

    @Test
    @DisplayName("a menu at the config root still produces a readable message")
    void rootSectionMessageIsReadable() {
        MemorySection s = section("", "title: '&aShop'\nrows: 3\n");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SimpleMenuLoader.loadMenu(s));
        assertTrue(e.getMessage().contains("(config root)"),
                "an empty path must not render as '': " + e.getMessage());
    }

    @Test
    @DisplayName("a menu WITH an icons block gets past the guard")
    void presentIconsBlockPassesTheGuard() {
        MemorySection s = section("menus.shop",
                "title: '&aShop'\nrows: 3\nicons:\n  filler:\n    material: STONE\n    slot: 0\n");
        // Icon loading itself needs a live server (XMaterial reads the running version), so this
        // only asserts that the guard is not what stops it.
        Throwable t = assertDoesNotThrow(() -> {
            try {
                SimpleMenuLoader.loadMenu(s);
                return null;
            } catch (Throwable caught) {
                return caught;
            }
        });
        if (t != null) {
            assertTrue(!(t instanceof IllegalArgumentException)
                            || !String.valueOf(t.getMessage()).contains("has no 'icons' block"),
                    "the icons guard rejected a section that HAS an icons block: " + t);
        }
    }
}
