package fr.uge.but.schtroumpf.model.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;

public class WeightedOutcomeSelector {
    private final List<OutcomeChoice> choices = new ArrayList<>();
    private final Random random = new Random();

    public WeightedOutcomeSelector addChoice(OutcomeChoice choice) {
        choices.add(choice);
        return this; // so we can do .addchoice().addchoice()
    }

    /**
     * filters out options where the resource is already full and selects 1 option based on
     * odds adjusted with modifiers
     **/
    public AbilityResult selectAndExecute(SmurfVillage village, OutcomeChoice fallback) {
        double successModifier = village.getModifiers().getDouble(GameModifierType.SUCCESS_CHANCE_BONUS);

        List<OutcomeChoice> validChoices = getValidAdjustedChoices(village, successModifier);
        double totalWeight = 0.0;
        for (OutcomeChoice choice : validChoices) {
        	totalWeight += choice.baseWeight();
        }

        // if no valid choices use fallback choice
        if (validChoices.isEmpty() || totalWeight <= 0.0) {
        	if (fallback == null) {
        		return buildEmptyResult();
        	}
            return buildResult(fallback);
        }

        // select random choice
        OutcomeChoice selectedChoice = selectChoiceByRoll(validChoices, totalWeight);

        // execute callback if present
        if (selectedChoice.onSuccessHook() != null) {
            selectedChoice.onSuccessHook().run();
        }

        return buildResult(selectedChoice);
    }

    /**
     * removes choices which produced resources are already full
     * @return valid choices
     **/
    private List<OutcomeChoice> getValidAdjustedChoices(SmurfVillage village, double successModifier) {
        List<OutcomeChoice> validChoices = new ArrayList<>();

        for (OutcomeChoice choice : this.choices) {
            if (isAnyResourceFull(village, choice.effects())) {
                continue;
            }

            double adjustedWeight = calculateAdjustedWeight(choice, successModifier);
            
            if (adjustedWeight > 0.0) {
                validChoices.add(new OutcomeChoice(
                    adjustedWeight,
                    choice.resultType(),
                    choice.messagePrefix(),
                    choice.effects(),
                    choice.onSuccessHook()
                ));
            }
        }
        return validChoices;
    }

    /** adjusts weight of choice based on success modifier */
    private double calculateAdjustedWeight(OutcomeChoice choice, double successModifier) {
        double adjustedWeight = choice.baseWeight();
        if (successModifier == 0.0) {
            return adjustedWeight;
        }

        if (choice.resultType() == fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType.SUCCESS) {
            return Math.max(0.0, adjustedWeight + successModifier);
        } 
        
        if (choice.resultType() == fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType.FAILURE) {
            return Math.max(0.0, adjustedWeight - successModifier);
        }

        return adjustedWeight;
    }

    private OutcomeChoice selectChoiceByRoll(List<OutcomeChoice> validChoices, double totalWeight) {
        double roll = random.nextDouble() * totalWeight;
        double currentSum = 0.0;

        for (OutcomeChoice choice : validChoices) {
            currentSum += choice.baseWeight();
            if (roll <= currentSum) {
                return choice;
            }
        }
        return validChoices.getLast();
    }

    /** block if any resource is already full */
    private boolean isAnyResourceFull(SmurfVillage village, List<ResourceEffect> effects) {
        for (ResourceEffect effect : effects) {
            if (effect.delta() > 0 && village.isResourceFull(effect.resourceType())) {
                return true;
            }
        }
        return false;
    }

    private AbilityResult buildResult(OutcomeChoice choice) {
    	String finalMessage = choice.messagePrefix();
    	for (var effect : choice.effects()) {
    		finalMessage += effect.toString() + " ";
    	}
        return new AbilityResult(choice.resultType(), finalMessage.stripLeading(), choice.effects());
    }

    private AbilityResult buildEmptyResult() {
        return new AbilityResult(AbilityResultType.NEUTRAL, "Rien ne s'est passé", List.of());
    }
}
