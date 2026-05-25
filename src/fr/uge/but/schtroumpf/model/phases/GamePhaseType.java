package fr.uge.but.schtroumpf.model.phases;

import java.nio.file.Path;

public enum GamePhaseType {
	PRODUCTION_PHASE(1, "Production", Path.of("phase_views/gui/ProductionView.fxml")),
	EVENT_PHASE(2, "Evenement aleatoire", Path.of("phase_views/gui/EventView.fxml")),
	COUNCIL_PHASE(3, "Conseil", Path.of("phase_views/gui/CouncilView.fxml")),
	CONSUMPTION_PHASE(4, "Consommation", Path.of("phase_views/gui/ConsumptionView.fxml")),
	CRISIS_PHASE(5, "Crise", Path.of("phase_views/gui/CrisisView.fxml"));
	
	private int code;
	private String displayName;
	private Path fxmlFile;
	
	GamePhaseType(int code, String displayName, Path fxmlFile) {
		this.code = code;
		this.displayName = displayName;
		this.fxmlFile = fxmlFile;
	}
	
	@Override
	public String toString() {
		return String.format("GamePhaseType: %s", this.name());
	}
	
	public int getCode() { return code; }
	public String getDisplayName() { return displayName; }
	public Path getFxmlFile() { return fxmlFile; }
}
