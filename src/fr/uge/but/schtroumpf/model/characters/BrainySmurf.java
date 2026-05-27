package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class BrainySmurf implements SmurfCharacter {
    private int energy = 10;
    private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();

    public BrainySmurf() {
        attributes.put(CharacterAttribute.WISDOM, 2);
    }

    @Override public SmurfType getType() { return SmurfType.BRAINY_SMURF; }
    @Override public int getEnergy() { return energy; }
	@Override public void setEnergy(int value) { energy = value; }

    @Override
    public void updateEnergy(SmurfVillage village, int delta) {
        final int finalMaxEnergy = village.getDynamicMaxEnergy(this);
        energy = Math.clamp(energy + delta, 0, finalMaxEnergy);
    }

    @Override public int getAttribute(CharacterAttribute attrib) { return attributes.getOrDefault(attrib, 0); }
    @Override public void updateAttribute(CharacterAttribute attrib, int delta) { attributes.put(attrib, getAttribute(attrib) + delta); }

    @Override
    public String toString() { return getType().getName(); }

    @Override
    public List<CharacterAbility> getAbilities() {
        // study a scroll
        CharacterAbility studyScroll = new CharacterAbility(
            "etudier un parchemin",
            "le schtroumpf a lunettes etudie, ce qui coute du savoir mais donnera un bonus futur.",
            2,
            List.of(
                new ResourceSnapshot(ResourceType.KNOWLEDGE, 1)
            ),
            List.of(
                new ResourceEffect(ResourceType.KNOWLEDGE, -1)
            ),
            this::executeStudyScroll
        );

        // translate formula
        CharacterAbility translateFormula = new CharacterAbility(
            "traduire une formule",
            "il traduit un vieux texte, ca donne de la defense ou du moral.",
            1,
            List.of(),
            List.of(),
            this::executeTranslateFormula
        );

        // write history
        CharacterAbility writeHistory = new CharacterAbility(
            "ecrire l'histoire",
            "il consigne les evenements pour annuler un malus futur.",
            3,
            List.of(),
            List.of(),
            this::executeWriteHistory
        );

        return List.of(
            studyScroll,
            translateFormula,
            writeHistory
        );
    }

    private AbilityResult executeStudyScroll(SmurfVillage village) {
        ResourceEffect minusKnowledge = new ResourceEffect(ResourceType.KNOWLEDGE, -1);
        return new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf a lunettes a etudie un parchemin pour plus tard : " + minusKnowledge,
            List.of(minusKnowledge)
        );
    }

    private AbilityResult executeTranslateFormula(SmurfVillage village) {
        ResourceEffect randomBonus;
        // 50% to get moral
        if (GameRandomness.randomChoice(0, 2) == 0) {
            randomBonus = new ResourceEffect(ResourceType.DEFENSE, 1);
        } else {
            randomBonus = new ResourceEffect(ResourceType.MORAL, 1);
        }

        return new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf a lunettes a traduit une formule : " + randomBonus,
            List.of(randomBonus)
        );
    }

    private AbilityResult executeWriteHistory(SmurfVillage village) {
        return new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf a lunettes ecrit l'histoire et annule un malus futur.",
            List.of()
        );
    }
}