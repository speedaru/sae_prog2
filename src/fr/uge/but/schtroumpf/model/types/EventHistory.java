package fr.uge.but.schtroumpf.model.types;

import module java.base;

import fr.uge.but.schtroumpf.model.events.GameEventType;

public record EventHistory(GameEventType eventType, List<ResourceEffect> effectsApplied, int round) { }
