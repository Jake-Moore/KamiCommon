package com.kamikazejam.kamicommon.menu.api.struct;

import com.kamikazejam.kamicommon.menu.api.struct.simple.SimpleMenuOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the drag-cancelling defaults that {@link MenuOptions}'s javadoc describes.
 * <p>
 * {@code cancelPlayerDragEvent} initialises to {@code true} while its javadoc said "Default:
 * false". Cancelling the drag is the safe behaviour and is what the field has always done, so the
 * javadoc was corrected rather than the field. This test exists so the next reader of that javadoc
 * cannot "fix" the field to agree with a comment that used to be wrong: flipping the initialiser
 * fails here, loudly, instead of quietly letting players drag items around inside a menu.
 * <p>
 * No live server is needed; these are plain fields on a plain object.
 */
class MenuOptionsTest {

    @Test
    @DisplayName("both drag cancels default to true")
    void dragCancelsDefaultToTrue() {
        SimpleMenuOptions options = new SimpleMenuOptions();
        assertTrue(options.isCancelDragEvent(), "cancelDragEvent must default to true");
        assertTrue(options.isCancelPlayerDragEvent(), "cancelPlayerDragEvent must default to true");
    }

    @Test
    @DisplayName("copy() carries both drag cancels")
    void copyCarriesDragCancels() {
        SimpleMenuOptions copied = new SimpleMenuOptions().copy();
        assertTrue(copied.isCancelDragEvent());
        assertTrue(copied.isCancelPlayerDragEvent());
    }

    @Test
    @DisplayName("copy() carries both drag cancels when they are turned off")
    void copyCarriesDisabledDragCancels() {
        SimpleMenuOptions off = new SimpleMenuOptions();
        off.setCancelDragEvent(false);
        off.setCancelPlayerDragEvent(false);
        SimpleMenuOptions copied = off.copy();
        assertFalse(copied.isCancelDragEvent());
        assertFalse(copied.isCancelPlayerDragEvent());
    }
}
