package com.kamikazejam.kamicommon.util;

import lombok.Getter;

import java.awt.Color;

@Getter
public enum Colors {
    BLACK('0', "black", new Color(0x000000)),
    DARK_BLUE('1', "dark_blue", new Color(0x0000AA)),
    DARK_GREEN('2', "dark_green", new Color(0x00AA00)),
    DARK_AQUA('3', "dark_aqua", new Color(0x00AAAA)),
    DARK_RED('4', "dark_red", new Color(0xAA0000)),
    DARK_PURPLE('5', "dark_purple", new Color(0xAA00AA)),
    GOLD('6', "gold", new Color(0xFFAA00)),
    GRAY('7', "gray", new Color(0xAAAAAA)),
    DARK_GRAY('8', "dark_gray", new Color(0x555555)),
    BLUE('9', "blue", new Color(0x5555FF)),
    GREEN('a', "green", new Color(0x55FF55)),
    AQUA('b', "aqua", new Color(0x55FFFF)),
    RED('c', "red", new Color(0xFF5555)),
    LIGHT_PURPLE('d', "light_purple", new Color(0xFF55FF)),
    YELLOW('e', "yellow", new Color(0xFFFF55)),
    WHITE('f', "white", new Color(0xFFFFFF)),
    ;

    private final char code;
    private final String name;
    private final Color color;
    Colors(char code, String name, Color color) {
        this.code = code;
        this.name = name;
        this.color = color;
    }
}
