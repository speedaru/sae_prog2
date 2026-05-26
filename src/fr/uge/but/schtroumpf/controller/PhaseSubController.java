package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;

public interface PhaseSubController {
	/** gives the phase sub controller a handle to call functions from the game fxWindow */
    void setMasterController(GameController masterController, Game game);
    
    void updateHudColors();
}
