module fr.uge.but.schtroumpf {
    // 1. Core Platform requirements
    requires java.base;
    
    // 2. UI Framework requirements (Fixes your compilation errors)
    requires javafx.controls;
    requires javafx.fxml;
    
    // 3. Serialization tool requirements
    requires com.fasterxml.jackson.databind;

    // 4. Allow JavaFX to inspect your window and phase controllers reflectively
    opens fr.uge.but.schtroumpf.controller to javafx.fxml;
    opens fr.uge.but.schtroumpf.controller.gui.windows to javafx.fxml;
    opens fr.uge.but.schtroumpf.controller.gui.phases to javafx.fxml;
    
    // 5. Keep your Jackson save-game mapping allowed
    opens fr.uge.but.schtroumpf.model.save to com.fasterxml.jackson.databind;
    
    // Optional: If you execute your main application launch from a class inside a package,
    // you may also need to export or open that package to javafx.graphics
    // exports fr.uge.but.schtroumpf; 
}
