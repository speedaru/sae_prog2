package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.SmurfVillage;

public record GamePhaseContext(SmurfVillage village, int currentRound) { }
