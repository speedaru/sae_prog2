package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;

public record GamePhaseContext(Game game, SmurfVillage village, int currentRound) { }
