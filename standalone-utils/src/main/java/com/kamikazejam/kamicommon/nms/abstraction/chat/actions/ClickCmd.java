package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class ClickCmd extends Click {
    private final String command;

    /**
     * @param command The command to run when clicked
     */
    ClickCmd(String command) {
        this.command = command;
    }
}

