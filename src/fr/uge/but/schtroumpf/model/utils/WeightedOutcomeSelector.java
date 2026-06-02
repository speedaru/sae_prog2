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
        return this;
    }

    public AbilityResult selectAndExecute(SmurfVillage village, OutcomeChoice fallback) {
        List<OutcomeChoice> validChoices = new ArrayList<>();
        double sumSuccess = 0.0, sumFailure = 0.0, sumNeutral = 0.0;

        // filter invalid choices and sum up weights by category
        for (OutcomeChoice choice : choices) {
            if (choice.baseWeight() <= 0.0 || isAnyResourceFull(village, choice.effects())) {
                continue;
            }
            validChoices.add(choice);
            
            if (choice.resultType() == AbilityResultType.SUCCESS) sumSuccess += choice.baseWeight();
            else if (choice.resultType() == AbilityResultType.FAILURE) sumFailure += choice.baseWeight();
            else sumNeutral += choice.baseWeight();
        }

        double totalRawWeight = sumSuccess + sumFailure + sumNeutral;

        // fallback if nothing is valid
        if (validChoices.isEmpty() || totalRawWeight <= 0.0) {
            return fallback != null ? buildResult(fallback) : buildEmptyResult();
        }

        // convert into probabilities 0.0 to 1.0
        double probSuccess = sumSuccess / totalRawWeight;
        double probFailure = sumFailure / totalRawWeight;
        double probNeutral = sumNeutral / totalRawWeight;
        
        Logger.LogDebug("success: %.2f, fail: %.2f, neutral: %.2f", probSuccess, probFailure, probNeutral);

        // apply modifier
        double modifier = village.getModifier(GameModifierType.SUCCESS_CHANCE_BONUS);
        double shift = Math.max(-probSuccess, Math.min(modifier, probFailure));

        double newProbSuccess = probSuccess + shift;
        double newProbFailure = probFailure - shift;
        
        // ensure probabilities add up to 1
        double checkSum = newProbSuccess + newProbFailure + probNeutral;
        if (Math.abs(checkSum - 1.0) > 0.0001) {
            throw new IllegalStateException("probability broken sum: " + checkSum);
        }

        double roll = random.nextDouble();
        double currentSum = 0.0;
        OutcomeChoice selectedChoice = validChoices.get(validChoices.size() - 1);

        for (OutcomeChoice choice : validChoices) {
            double finalProbability = 0.0;
            
            if (choice.resultType() == AbilityResultType.SUCCESS) {
                finalProbability = (choice.baseWeight() / sumSuccess) * newProbSuccess;
            } else if (choice.resultType() == AbilityResultType.FAILURE) {
                finalProbability = (choice.baseWeight() / sumFailure) * newProbFailure;
            } else {
                finalProbability = (choice.baseWeight() / sumNeutral) * probNeutral;
            }

            currentSum += finalProbability;
            if (roll <= currentSum) {
                selectedChoice = choice;
                break;
            }
        }

        // execute hooks
        if (selectedChoice.onSuccessHook() != null) {
            selectedChoice.onSuccessHook().run();
        }

        return buildResult(selectedChoice);
    }

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
            finalMessage += " " + effect.toString();
        }
        return new AbilityResult(choice.resultType(), finalMessage.stripLeading(), choice.effects());
    }

    private AbilityResult buildEmptyResult() {
        return new AbilityResult(AbilityResultType.NEUTRAL, "Rien ne s'est passé", List.of());
    }
}
