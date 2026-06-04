package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.types.CodeEnum;

public enum GamePhaseType implements CodeEnum {
	PRODUCTION_PHASE(1, "Production", "/view/phase_views/ProductionView.fxml"),
	EVENT_PHASE(2, "Evenement aleatoire", "/view/phase_views/EventView.fxml"),
	COUNCIL_PHASE(3, "Conseil", "/view/phase_views/CouncilView.fxml"),
	CONSUMPTION_PHASE(4, "Consommation", "/view/phase_views/ConsumptionView.fxml"),
	CRISIS_PHASE(5, "Crise", "/view/phase_views/CrisisView.fxml"),

	// end phases
	VICTORY(6, "Victoire", "/view/phase_views/VictoryView.fxml"),
	DEFEAT(7, "Defaite", "/view/phase_views/DefeatView.fxml");
	
	private int code;
	private String displayName;
	private String fxmlFile;
	
	GamePhaseType(int code, String displayName, String fxmlFile) {
		this.code = code;
		this.displayName = displayName;
		this.fxmlFile = fxmlFile;
	}
	
	@Override
	public String toString() {
		return String.format("GamePhaseType: %s", this.name());
	}
	
	@Override public int getCode() { return code; }
	public String getDisplayName() { return displayName; }
	public String getFxmlFile() { return fxmlFile; }
}
