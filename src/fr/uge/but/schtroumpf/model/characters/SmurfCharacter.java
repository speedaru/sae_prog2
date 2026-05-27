package fr.uge.but.schtroumpf.model.characters;

import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;

public interface SmurfCharacter {
    SmurfType getType();
    
    int getEnergy();
    void setEnergy(int value);
    void updateEnergy(SmurfVillage village, int delta);
    default int getBaseMaxEnergy() { return 10; }

    // Dynamic attributes tracking (Sagesse, Individual Moral, etc. as required by PDF)
    int getAttribute(CharacterAttribute attrib);
    void updateAttribute(CharacterAttribute attrib, int delta);

    List<CharacterAbility> getAbilities();
    
    default boolean hasEnoughEnergy(CharacterAbility ability) { return getEnergy() >= ability.energyCost(); }
    default boolean hasRequiredResources(SmurfVillage village, CharacterAbility ability) { return village.verifyResources(ability.requiredResources()); }
    default boolean canExecute(SmurfVillage village, CharacterAbility ability) { return hasEnoughEnergy(ability) && hasRequiredResources(village, ability); }
    
    static public SmurfCharacter fromType(SmurfType type) {
    	return switch (type) {
		case BRAINY_SMURF -> new BrainySmurf();
		case GLUTTON_SMURF -> new GluttonSmurf();
		case GRAND_SMURF -> new GrandSmurf();
		case GROUCHY_SMURF -> new GrouchySmurf();
		case HANDY_SMURF -> new HandySmurf();
		case SMURFETTE -> new Smurfette();
		default -> throw new IllegalArgumentException("type with no smurf character");
    	};
    }
}
