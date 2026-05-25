package fr.uge.but.schtroumpf.model.characters;

import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;

public interface SmurfCharacter {
    SmurfType getType();
    
    int getEnergy();
    void updateEnergy(SmurfVillage village, int delta);
    default int getBaseMaxEnergy() { return 10; }

    // Dynamic attributes tracking (Sagesse, Individual Moral, etc. as required by PDF)
    int getAttribute(CharacterAttribute attrib);
    void updateAttribute(CharacterAttribute attrib, int delta);

    List<CharacterAbility> getAbilities();
    
    default boolean hasEnoughEnergy(CharacterAbility ability) { return getEnergy() >= ability.energyCost(); }
    default boolean hasRequiredResources(SmurfVillage village, CharacterAbility ability) { return village.verifyResources(ability.requiredResources()); }
    default boolean canExecute(SmurfVillage village, CharacterAbility ability) { return hasEnoughEnergy(ability) && hasRequiredResources(village, ability); }
}
