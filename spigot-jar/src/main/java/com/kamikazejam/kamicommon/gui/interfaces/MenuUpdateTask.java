package com.kamikazejam.kamicommon.gui.interfaces;

@SuppressWarnings("unused")
public interface MenuUpdateTask {

    void onUpdate(MenuTicked menu);

    int getLoopTicks();

}
