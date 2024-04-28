package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class ClickUrl extends Click {
    private final String url;

    /**
     * @param url The url to prompt when clicked
     */
    ClickUrl(String url) {
        this.url = url;
    }
}

