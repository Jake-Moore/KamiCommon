package com.kamikazejam.kamicommon.menu.api.loaders.menu;

import com.kamikazejam.kamicommon.yaml.spigot.MemorySection;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Pins the fact that a menu section is allowed to have no {@code icons:} block.
 * <p>
 * {@code getConfigurationSection} is {@code @NotNull} and hands back an EMPTY section for a key
 * that is not present, so a menu with no {@code icons:} block loads with no icons and keeps
 * whatever {@code filler:} it declares. That is deliberate and it is the common shape: most
 * menu-shaped configs in use have no {@code icons:} block at all. alpha.50 briefly rejected them;
 * this file exists so that cannot come back, as neither a throw nor a warning.
 * <p>
 * <b>How these assert without a server.</b> Bukkit is not on this module's test classpath, so
 * {@code loadMenu} cannot run to completion: it dies inside {@code new SimpleMenu.Builder(...)}
 * with a {@link LinkageError} for a missing Bukkit type. Reaching that point is the assertion.
 * Every config-shaped rejection {@code loadMenu} can make happens strictly before it, so a
 * {@link LinkageError} proves the section was accepted, and a config-shaped exception proves it
 * was not. Nothing here depends on the error staying a {@link LinkageError}: if Bukkit is ever
 * added to the test classpath these keep passing, because completing normally is also an accept.
 */
class SimpleMenuLoaderTest {

    private static MemorySection section(String path, String yaml) {
        MappingNode node = (MappingNode) new Yaml().compose(new StringReader(yaml));
        return new MemorySection(node, path, null);
    }

    /** Runs the loader and returns whatever it threw, or null if it completed. */
    private static @Nullable Throwable load(MemorySection section) {
        try {
            SimpleMenuLoader.loadMenu(section);
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

    /** Asserts the loader ACCEPTED this section, meaning it got as far as building the menu. */
    private static void assertAccepted(MemorySection section, String what) {
        Throwable t = load(section);
        if (t == null || t instanceof LinkageError) { return; }
        fail("loadMenu rejected " + what + ", which it must accept: "
                + t.getClass().getName() + ": " + t.getMessage());
    }

    @Test
    @DisplayName("a menu with no icons block loads")
    void missingIconsBlockLoads() {
        assertAccepted(section("menus.shop", "title: '&aShop'\nrows: 3\n"),
                "a menu section with no icons block");
    }

    @Test
    @DisplayName("an icons key with nothing under it loads")
    void emptyIconsBlockLoads() {
        assertAccepted(section("menus.shop", "title: '&aShop'\nrows: 3\nicons:\n"),
                "a menu section with an empty icons block");
    }

    @Test
    @DisplayName("a menu with no icons block at the config root loads")
    void rootSectionWithNoIconsLoads() {
        assertAccepted(section("", "title: '&aShop'\nrows: 3\n"),
                "a menu section at the config root with no icons block");
    }

    @Test
    @DisplayName("a filler-only menu loads, which is the common shape")
    void fillerOnlyMenuLoads() {
        assertAccepted(section("features.backpack.menu",
                        "title: '&aBackpack'\nrows: 6\nfiller:\n  material: BLACK_STAINED_GLASS_PANE\n"),
                "a menu section with a filler and no icons block");
    }

    @Test
    @DisplayName("a menu WITH an icons block still loads")
    void presentIconsBlockLoads() {
        assertAccepted(section("menus.shop",
                        "title: '&aShop'\nrows: 3\nicons:\n  sword:\n    material: STONE\n    slot: 0\n"),
                "a menu section with an icons block");
    }

    @Test
    @DisplayName("an unusable size is still the error it always was")
    void unusableSizeStillFails() {
        // The guard alpha.50 added ran before this one and would have masked it. Nothing to do
        // with icons, and the point: the ONLY thing loadMenu rejects a section for is its size.
        Throwable t = load(section("menus.shop", "title: '&aShop'\n"));
        assertTrue(t instanceof IllegalStateException,
                "expected IllegalStateException for a menu with no rows and no type, got " + t);
        assertTrue(String.valueOf(t.getMessage()).contains("menus.shop"),
                "the size error must still name the section: " + t.getMessage());
    }

    // An unparseable 'type:' cannot be covered here. MenuSizeLoader reaches InventoryType.valueOf,
    // and InventoryType is a Bukkit enum, so off-server that raises a LinkageError instead of the
    // IllegalStateException a real server raises. The no-rows-and-no-type case above exercises the
    // same throw without needing Bukkit.
}
