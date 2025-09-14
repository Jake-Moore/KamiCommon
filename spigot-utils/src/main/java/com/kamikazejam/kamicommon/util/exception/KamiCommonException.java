package com.kamikazejam.kamicommon.util.exception;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.LegacyColors;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@Getter
@SuppressWarnings({"unused"})
public class KamiCommonException extends Exception {

    // -------------------------------------------- //
    // MESSAGES
    // -------------------------------------------- //

    protected @Nullable VersionedComponent message = null;

    // Empty constructor, to add messages use one of the below methods
    public KamiCommonException() {
    }

    public boolean hasMessage() {
        return this.message != null;
    }

    @Override
    public @Nullable String getMessage() {
        if (this.message == null) { return null; }
        return message.plainText();
    }

    public @Nullable VersionedComponent getComponent() {
        if (this.message == null) { return null; }
        return message;
    }

    // Will be removed once most uses of the old Txt format are gone
    private static final Pattern pattern = Pattern.compile("<(.+?)>");
    private static void checkForOldTxtFormat(@NotNull String msg) {
        if (!pattern.matcher(msg).find()) { return; }
        SpigotUtilsSource.get().getColorComponentLogger().warn(
                NmsAPI.getVersionedComponentSerializer().fromPlainText(
                        "Old Txt format detected in KamiCommonException message: " + msg
                )
        );
    }

    // Add single

    /**
     * @deprecated Use {@link #addMsgFromLegacyColors(String)} instead for clarity
     */
    @Deprecated
    public @NotNull KamiCommonException addMsg(@Nullable String msg) {
        return this.addMsgFromLegacyColors(msg);
    }
    public @NotNull KamiCommonException addMsgFromLegacyColors(@Nullable String msg) {
        if (msg == null) { return this; }
        checkForOldTxtFormat(msg);
        this.message = NmsAPI.getVersionedComponentSerializer().fromLegacySection(LegacyColors.t(msg));
        return this;
    }

    /**
     * @deprecated Use {@link #addMsgFromLegacyColors(String, Object...)} instead for clarity
     */
    @Deprecated
    public @NotNull KamiCommonException addMsg(@Nullable String msg, Object... args) {
        return this.addMsgFromLegacyColors(msg, args);
    }
    public @NotNull KamiCommonException addMsgFromLegacyColors(@Nullable String msg, Object... args) {
        if (msg == null) { return this; }
        checkForOldTxtFormat(msg);
        return this.addMsgFromLegacyColors(String.format(msg, args));
    }

    public @NotNull KamiCommonException addMsg(@NotNull VersionedComponent msg) {
        this.message = msg;
        return this;
    }

    public @NotNull KamiCommonException addMsgFromMiniMessage(@Nullable String miniMessage) {
        if (miniMessage == null) { return this; }
        this.message = NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessage);
        return this;
    }

    public @NotNull KamiCommonException addMsgFromMiniMessage(@Nullable String miniMessage, Object... args) {
        if (miniMessage == null) { return this; }
        return this.addMsgFromMiniMessage(String.format(miniMessage, args));
    }

    public @NotNull KamiCommonException addMsgFromPlainText(@Nullable String plainText) {
        if (plainText == null) { return this; }
        return this.addMsg(NmsAPI.getVersionedComponentSerializer().fromPlainText(plainText));
    }
}
