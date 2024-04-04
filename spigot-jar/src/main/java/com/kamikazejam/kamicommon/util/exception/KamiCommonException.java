package com.kamikazejam.kamicommon.util.exception;

import com.kamikazejam.kamicommon.util.mson.Mson;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("unused")
@Getter
public class KamiCommonException extends Exception {

    // -------------------------------------------- //
    // MESSAGES
    // -------------------------------------------- //

    protected Mson messages = Mson.mson();

    // Empty constructor, to add messages use one of the below methods
    public KamiCommonException() {
    }

    public boolean hasMessages() {
        return !this.messages.isEmpty();
    }

    @Override
    public @NotNull String getMessage() {
        return this.messages.toPlain(true);
    }

    // Set single
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException setMessage(@NotNull Object part) {
        this.messages = Mson.mson(part);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException setMsg(@NotNull String msg) {
        this.messages = Mson.parse(msg);
        return this;
    }

    @Contract(value = "_, _ -> this", mutates = "this")
    public @NotNull KamiCommonException setMsg(String msg, Object... objects) {
        this.messages = Mson.parse(msg, objects);
        return this;
    }

    // Add single
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException addMessage(@NotNull Object part) {
        // Only add a newline if not empty.
        Mson mson = this.messages.isEmpty() ? Mson.mson(part) : Mson.mson("\n", part);
        this.messages = this.messages.add(mson);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException addMsg(@NotNull String msg) {
        return this.addMessage(Mson.parse(msg));
    }

    @Contract(value = "_, _ -> this", mutates = "this")
    public @NotNull KamiCommonException addMsg(String msg, Object... args) {
        return this.addMessage(Mson.parse(msg, args));
    }

    // Set several
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException setMsgs(@NotNull Collection<@NotNull String> msgs) {
        this.messages = Mson.parse(msgs);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException setMsgs(String @NotNull ... msgs) {
        return this.setMsgs(Arrays.asList(msgs));
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException addMsgs(@NotNull Collection<@NotNull String> msgs) {
        return this.addMessage(Mson.parse(msgs));
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull KamiCommonException addMsgs(@NotNull String @NotNull ... msgs) {
        return this.addMsgs(Arrays.asList(msgs));
    }

}
