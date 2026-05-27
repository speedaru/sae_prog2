package fr.uge.but.schtroumpf;

import module java.base;

import fr.uge.but.schtroumpf.view.MainWindow;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.model.utils.Logger.LoggerFlag;;

public class Main {
	public static void main(String[] args) {
		// init logger
		List<LoggerFlag> flags = List.of(
				LoggerFlag.DEBUG,
				LoggerFlag.WARNING,
				LoggerFlag.ERROR,
				LoggerFlag.TRACE
		);
		Logger.Init(flags);
		
		// test logger
		Logger.LogDebug("hello");
		
		GameSaveManager.init();
		
		// launch fxml fxWindow
		MainWindow app = new MainWindow();
		app.launch();
	}
}
