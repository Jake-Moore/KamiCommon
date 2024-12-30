package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.util.Preconditions;
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
    private final @Nullable DefaultValue<T> defaultValue; // If null, then no default value was supplied, thus the param is required
    private final boolean requiredFromConsole;
    private final boolean concatFromHere;

    // Private constructor only accessible by Builder
    private Parameter(@NotNull Builder<T> builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.defaultValue = builder.defaultValue;
        this.requiredFromConsole = builder.requiredFromConsole;
        this.concatFromHere = builder.concatFromHere;
    }

    // -------------------------------------------- //
    // CONVENIENCE METHODS
    // -------------------------------------------- //

    public boolean isRequired() {
        // If null, then no default value was supplied, thus the param is required
        return this.defaultValue == null;
    }

    public boolean isOptional() {
        return !this.isRequired();
    }

    /**
     * @return IFF a default value is set (may still be null)
     */
    public boolean isDefaultValueSet() {
        return this.defaultValue != null;
    }

    /**
     * @return null if no default value is set, or the description of the default value if set, otherwise the default value as a string
     */
    public @Nullable String getDefaultDesc() {
        if (this.defaultValue == null) { return null; }
        if (this.defaultValue.isDescriptionSet()) { return this.defaultValue.getDescription(); }
        return String.valueOf(this.defaultValue.getValue());
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



    // -------------------------------------------- //
    // BUILDER
    // -------------------------------------------- //

    @Getter @Setter
    @SuppressWarnings("unused")
    @Accessors(chain = true, fluent = true)
    public static class Builder<T> {
        // Required parameters
        private final @NotNull Type<T> type;

        // Optional parameters - initialized to default values
        private @NotNull String name;
        private @Nullable DefaultValue<T> defaultValue = null;
        private boolean requiredFromConsole = false;
        private boolean concatFromHere = false;

        public Builder(@NotNull Type<T> type) {
            Preconditions.checkNotNull(type, "type cannot be null");
            this.type = type;
            this.name = Objects.requireNonNull(type.getName()); // Default name to type name
        }

        // Other options for the defaultValue setter
        public Parameter.Builder<T> defaultValue(@Nullable T value) {
            this.defaultValue = new DefaultValue<>(value, null);
            return this;
        }
        public Parameter.Builder<T> defaultValue(@Nullable T value, @Nullable String description) {
            this.defaultValue = new DefaultValue<>(value, description);
            return this;
        }

        @NotNull
        public Parameter<T> build() {
            return new Parameter<>(this);
        }
    }

    @NotNull
    public static <T> Parameter.Builder<T> builder(@NotNull Type<T> type) {
        return new Parameter.Builder<>(type);
    }
    @NotNull
    public static <T> Parameter.Builder<T> of(@NotNull Type<T> type) {
        return builder(type);
    }
}
