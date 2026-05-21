package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.gui.windows.AppController;

public interface WindowSubController {
	/** gives the FXML sub controller a handle to request window switches */
    void setRouter(AppController router);
}
