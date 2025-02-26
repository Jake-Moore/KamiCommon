package com.kamikazejam.kamicommon.menu.api.struct.oneclick;

import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * A container for all the options that every {@link com.kamikazejam.kamicommon.menu.OneClickMenu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Setter @Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class OneClickMenuOptions extends MenuOptions {
    private boolean excludeFillerClickFromOneClick = true;

    public OneClickMenuOptions() {}
    // Copy Constructor
    public OneClickMenuOptions(@NotNull OneClickMenuOptions copy) {
        this.excludeFillerClickFromOneClick = copy.excludeFillerClickFromOneClick;
    }

    public interface OneClickMenuOptionsModification {
        void modify(@NotNull OneClickMenuOptions options);
    }

    @Override
    public @NotNull OneClickMenuOptions copy() {
        OneClickMenuOptions copy = new OneClickMenuOptions(this);
        this.copyInto(copy);
        return copy;
    }
}
