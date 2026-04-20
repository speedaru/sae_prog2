package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.view.windows.GameWindow;

public record GamePhaseContext(GameWindow window, SmurfVillage village, int currentRound) { }
