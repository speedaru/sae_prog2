package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.gui.AppController;

public interface FxmlSubController {
	/** gives the FXML sub controller a handle to request window switches */
    void setRouter(AppController router);
}
