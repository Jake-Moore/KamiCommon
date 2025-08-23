package com.kamikazejam.kamicommon.menu.api.struct.simple;

import com.kamikazejam.kamicommon.menu.SimpleMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * A container for all the options that every {@link SimpleMenu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class SimpleMenuOptions extends MenuOptions<SimpleMenu> {
    public SimpleMenuOptions() {
        // Default constructor initializes the options with default values
    }

    // Copy Constructor
    private SimpleMenuOptions(@NotNull SimpleMenuOptions copy) {
        // no fields to copy yet
    }

    @Override
    public @NotNull SimpleMenuOptions copy() {
        // Use copy constructor to create a new instance of SimpleMenuOptions
        SimpleMenuOptions copy = new SimpleMenuOptions(this);
        // Copy base options from MenuOptions abstract class
        this.copyInto(copy);
        return copy;
    }
}
