package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.SmurfVillage;

public record GamePhaseContext(GameController masterController, SmurfVillage village, int currentRound) { }
