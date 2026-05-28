package fr.uge.but.schtroumpf.model.types;

import java.util.Objects;

public record GameModifierEffect<T>(GameModifierType type, T value) {
	public GameModifierEffect {
        Objects.requireNonNull(type, "GameModifierType cannot be null.");
        Objects.requireNonNull(value, "Modifier value cannot be null.");

        // ensure T is the type of type.type
        if (!type.getType().isInstance(value)) {
			throw new IllegalArgumentException(String.format("T: %s is not the same type as %s: %s",
					value.getClass().getSimpleName(), type.name(), type.getType().getSimpleName()));
        }
    }
}
