package com.kamikazejam.kamicommon.util.exception;

import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings({"unused"})
public class KamiCommonException extends Exception {

    // -------------------------------------------- //
    // MESSAGES
    // -------------------------------------------- //

    protected @Nullable KMessageSingle message = null;

    // Empty constructor, to add messages use one of the below methods
    public KamiCommonException() {
    }

    public boolean hasMessage() {
        return this.message != null;
    }

    @Override
    public @Nullable String getMessage() {
        if (this.message == null) { return null; }
        return message.getLine();
    }

    public @Nullable KMessageSingle getKMessage() {
        if (this.message == null) { return null; }
        return message;
    }

    // Add single
    public @NotNull KamiCommonException addMsg(@Nullable String msg) {
        if (msg == null) { return this; }
        this.message = new KMessageSingle(StringUtil.t(msg));
        return this;
    }

    public @NotNull KamiCommonException addMsg(@Nullable String msg, Object... args) {
        if (msg == null) { return this; }
        return this.addMsg(String.format(StringUtil.t(msg), args));
    }

    public @NotNull KamiCommonException addMsg(@NotNull KMessageSingle msg) {
        this.message = msg;
        return this;
    }
}
