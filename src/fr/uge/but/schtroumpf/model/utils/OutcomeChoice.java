package fr.uge.but.schtroumpf.model.utils;

import java.util.List;
import java.util.Objects;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;

/**
 * Represents a single potential result of a random operation.
 */
public record OutcomeChoice(
    double baseWeight,
    AbilityResultType resultType,
    String messagePrefix,
    List<ResourceEffect> effects,
    Runnable onSuccessHook // optional for executing stuff like updateAttribute()
) {
    public OutcomeChoice {
        Objects.requireNonNull(resultType);
        Objects.requireNonNull(messagePrefix);
        Objects.requireNonNull(effects);
        if (baseWeight < 0.0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
    }

    /** simple constructor for simple choices without callbacks */
    public OutcomeChoice(double baseWeight, AbilityResultType resultType, String messagePrefix, List<ResourceEffect> effects) {
        this(baseWeight, resultType, messagePrefix, effects, null);
    }
}
