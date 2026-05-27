package fr.uge.but.schtroumpf.model.types;

import fr.uge.but.schtroumpf.model.save.GameSave;

public class VillageModifierContext {
	private double successChanceBonus = 0.0;
    private int energyRechargeRateDelta = 0; // < 0 is slower, > 0 is faster
    private int maxEnergyDelta = 0;
    private double efficiencyMultiplier = 1.0;
    private boolean passiveFoodProductionBlocked = false;
    
    public VillageModifierContext() {
    }
    
    public VillageModifierContext(GameSave.VillageModifierCtxState state) {
    	this.successChanceBonus = state.successChanceBonus();
    	this.energyRechargeRateDelta = state.energyRechargeRateDelta();
    	this.maxEnergyDelta = state.maxEnergyDelta();
    	this.efficiencyMultiplier = state.efficiencyMultiplier();
    	this.passiveFoodProductionBlocked = state.passiveFoodProductionBlocked();
    }
    
    // ------------------------- adders / setters
    
    public void addSuccessChanceBonus(double bonus) { 
        this.successChanceBonus += bonus; 
    }

    public void addEnergyRechargeRateDelta(double factor) { 
        this.energyRechargeRateDelta += factor; 
    }
    
    public void addMaxEnergyDelta(int delta) { 
        this.maxEnergyDelta += delta; 
    }
    
    public void multiplyEfficiency(double factor) { 
        this.efficiencyMultiplier *= factor; 
    }
    
    public void setPassiveFoodProductionBlocked(boolean blocked) { 
        this.passiveFoodProductionBlocked = blocked; 
    }

    // ------------------------- getters
    
	public double getSuccessChanceBonus() { return successChanceBonus; }
	public int getEnergyRechargeRateDelta() { return energyRechargeRateDelta; }
    public int getMaxEnergyDelta() { return maxEnergyDelta; }
    public double getEfficiencyMultiplier() { return efficiencyMultiplier; }
    public boolean isPassiveFoodProductionBlocked() { return passiveFoodProductionBlocked; }
}
