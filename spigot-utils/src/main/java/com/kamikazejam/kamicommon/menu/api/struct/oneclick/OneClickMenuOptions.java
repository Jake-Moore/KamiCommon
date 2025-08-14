package com.kamikazejam.kamicommon.menu.api.struct.oneclick;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.OneClickMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * A container for all the options that every {@link com.kamikazejam.kamicommon.menu.OneClickMenu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Setter
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class OneClickMenuOptions extends MenuOptions<OneClickMenu> {
    private boolean excludeFillerClickFromOneClick;

    public OneClickMenuOptions() {
        this.excludeFillerClickFromOneClick = Defaults.isExcludeFillerClickFromOneClick();
    }

    // Copy Constructor
    public OneClickMenuOptions(@NotNull OneClickMenuOptions copy) {
        this.excludeFillerClickFromOneClick = copy.excludeFillerClickFromOneClick;
    }

    public interface OneClickMenuOptionsModification {
        <T extends Menu<T>> void modify(@NotNull OneClickMenuOptions options);
    }

    @Override
    public @NotNull OneClickMenuOptions copy() {
        // Use copy constructor to copy one click options
        OneClickMenuOptions copy = new OneClickMenuOptions(this);
        // Copy base options from MenuOptions abstract class
        this.copyInto(copy);
        return copy;
    }

    public static class Defaults {
        @Getter @Setter
        private static boolean excludeFillerClickFromOneClick = true;
    }
}
