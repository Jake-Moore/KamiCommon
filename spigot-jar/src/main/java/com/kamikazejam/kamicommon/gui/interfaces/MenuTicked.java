package com.kamikazejam.kamicommon.gui.interfaces;

import java.util.List;

public interface MenuTicked extends Menu {

    void addUpdateHandlerSubTask(MenuUpdateTask subTask);

    List<MenuUpdateTask> getUpdateSubTasks();

}
