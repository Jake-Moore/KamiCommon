package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class ClickSuggest extends Click {
    private final String suggestion;

    /**
     * @param suggestion The text/command to suggest in the player's chat box, when clicked
     */
    ClickSuggest(String suggestion) {
        this.suggestion = suggestion;
    }
}

