package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.command.type.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class Parameter<T> {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final @NotNull Type<T> type;
    private final @NotNull String name;
    // TODO EXTRACT THE THREE 'DEFAULT' FIELDS INTO A SINGLE DATA CLASS
    //   The use of the boolean defaultValueSet can be replaced with nullability checks on this new data field
    private final @Nullable T defaultValue;
    private final boolean defaultValueSet;
    private final @Nullable String defaultDesc;

    private final boolean requiredFromConsole;

    // Private constructor only accessible by Builder
    private Parameter(@NotNull Builder<T> builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.defaultValue = builder.defaultValue;
        this.defaultValueSet = builder.defaultValueSet;
        this.defaultDesc = builder.defaultDesc;
        this.requiredFromConsole = builder.requiredFromConsole;
    }

    // -------------------------------------------- //
    // BUILDER
    // -------------------------------------------- //

    @Getter @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder<T> {
        // Required parameters
        private final @NotNull Type<T> type;

        // Optional parameters - initialized to default values
        private @NotNull String name;
        private @Nullable T defaultValue = null;
        private boolean defaultValueSet = false;
        private @Nullable String defaultDesc = null;
        private boolean requiredFromConsole = false;

        public Builder(@NotNull Type<T> type) {
            this.type = type;
            this.name = Objects.requireNonNull(type.getName()); // Default name to type name
        }

        public Parameter<T> build() {
            return new Parameter<>(this);
        }
    }

    // -------------------------------------------- //
    // CONVENIENCE METHODS
    // -------------------------------------------- //

    public boolean isRequired() {
        return this.getDefaultDesc() == null;
    }

    public boolean isOptional() {
        return !this.isRequired();
    }

    public @Nullable String getDefaultDesc() {
        if (this.defaultDesc != null) return defaultDesc;
        if (this.defaultValueSet) return String.valueOf(this.defaultValue);
        return null;
    }

    public boolean isRequiredFor(@Nullable CommandSender sender) {
        if (this.isRequired()) return true;
        if (!this.requiredFromConsole) return false;
        if (sender == null) return false;
        return !(sender instanceof Player);
    }

    public boolean isOptionalFor(CommandSender sender) {
        return !this.isRequiredFor(sender);
    }

    @NotNull
    public String getTemplate(@Nullable CommandSender sender) {
        String ret;

        if (this.isRequiredFor(sender)) {
            ret = "<" + this.getName() + ">";
        } else {
            @Nullable String def = getDefaultDesc();
            ret = "[" + this.getName() + (def != null ? "=" + def : "") + "]";
        }
        return ret;
    }
}
