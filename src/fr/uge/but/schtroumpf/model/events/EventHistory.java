package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.characters.*;

public record EventHistory(GameEventType eventType, List<ResourceEffect> effectsApplied, int round) { }
