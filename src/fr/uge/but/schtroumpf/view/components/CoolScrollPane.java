package fr.uge.but.schtroumpf.view.components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class CoolScrollPane extends ScrollPane {
	public CoolScrollPane() {
		super();
		
		setScrollBarStyle(this);
	}
	
	public static void setScrollBarStyle(ScrollPane scrollPane) {
    	Platform.runLater(() -> {
    		scrollPane.lookupAll(".scroll-bar").forEach(scrollBar -> {
    			Node thumb = null;
    			while (thumb == null) { // busy wait
    				thumb = scrollBar.lookup(".thumb");
    			}
    			
				thumb.setStyle("""
					-fx-background-color: #252525;
					-fx-background-radius: 10;
					-fx-pref-width: 10;
				""");

    			Node track = null;
    			while (track == null) {
					track = scrollBar.lookup(".track");
    			}
    			
				track.setStyle("-fx-background-color: #555555;");
    		});
    	});
	}
}
