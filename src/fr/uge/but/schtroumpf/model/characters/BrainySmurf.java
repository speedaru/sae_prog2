package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.*;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ModifierEffect;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.OutcomeChoice;
import fr.uge.but.schtroumpf.model.utils.WeightedOutcomeSelector;

public class BrainySmurf implements SmurfCharacter {
    private int energy = 10;
    private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();

    public BrainySmurf() {
        attributes.put(CharacterAttribute.WISDOM, 0);
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
    @Override public void setAttribute(CharacterAttribute attrib, int value) { attributes.put(attrib, value); }
    @Override public void updateAttribute(CharacterAttribute attrib, int delta) { attributes.put(attrib, getAttribute(attrib) + delta); }

    @Override
    public String toString() { return getType().getName(); }

    @Override
    public List<CharacterAbility> getAbilities() {
        // translate formula
        CharacterAbility translateFormula = new CharacterAbility(
            "traduire une formule",
            "le schtroumpf a lunettes traduit un vieux texte, donne du savoir en cas de succès.",
            2,
            List.of(),
            List.of(
            	new PossibleBranch("Succès :", List.of(
					new ResourceEffect(ResourceType.KNOWLEDGE, 1)
				)),
				new PossibleBranch("Echec :", List.of())
			),
            this::executeTranslateFormula
        );

        // study a scroll
        CharacterAbility studyScroll = new CharacterAbility(
            "etudier un parchemin",
            "le schtroumpf a lunettes etudie un parchemin. Consomme du savoir mais permet de produire plus de resources "
            + "le mois prochain",
            2,
            List.of(
                new ResourceSnapshot(ResourceType.KNOWLEDGE, 1)
            ),
            List.of(
            	new PossibleBranch("Effets :", List.of(
					new ResourceEffect(ResourceType.KNOWLEDGE, -1)
				))
            ),
            this::executeStudyScroll
        );

        // write history
        CharacterAbility writeHistory = new CharacterAbility(
            "etudie l'histoire",
            "le schtroumpf a lunettes predit l'avenir et augmente la chance du village de 50% pendant 2 mois.",
            3,
            List.of(new ResourceSnapshot(ResourceType.KNOWLEDGE, 3)),
            List.of(),
            this::executeWriteHistory
        );

        return List.of(
            translateFormula,
            studyScroll,
            writeHistory
        );
    }

    private AbilityResult executeStudyScroll(SmurfVillage village) {
        ResourceEffect minusKnowledge = new ResourceEffect(ResourceType.KNOWLEDGE, -1);
        ModifierEffect moreProduction = new ModifierEffect(GameModifierType.PRODUCTION_DELTA, 2, 1, false);
        
        GameModifierType type = moreProduction.getType();
        String moreProductionStr = String.format("%s %s", type.formatDisplayValue(moreProduction.getValue()), type.getName());
        
        village.accumulateTempModifier(moreProduction);

        return new AbilityResult(
            AbilityResultType.NEUTRAL,
            String.format("le schtroumpf a lunettes a etudie un parchemin %s %s", minusKnowledge, moreProductionStr),
            List.of(minusKnowledge)
        );
    }

    private AbilityResult executeTranslateFormula(SmurfVillage village) {
        double chance = 0.5 + getAttribute(CharacterAttribute.WISDOM) * 0.05;

        return new WeightedOutcomeSelector()
        	.addChoice(new OutcomeChoice(
				chance,
        		AbilityResultType.SUCCESS,
        		"le schtroumpf a lunettes a traduit une formule ",
        		List.of(new ResourceEffect(ResourceType.KNOWLEDGE, 1)),
        		() -> updateAttribute(CharacterAttribute.WISDOM, 1)
			))
        	.addChoice(new OutcomeChoice(
        		1 - chance,
        		AbilityResultType.FAILURE,
        		"le schtroumpf a lunettes n'a pas pu traduire la formule !",
        		List.of()
			))
        	.selectAndExecute(village, null);
    }

    private AbilityResult executeWriteHistory(SmurfVillage village) {
    	AbilityResult res = new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf a lunettes a augmente la chance de 50%.",
            List.of()
        );
    	
		village.accumulateTempModifier(new ModifierEffect(GameModifierType.SUCCESS_CHANCE_BONUS, 0.5, 1, false));

        return res;
    }
}