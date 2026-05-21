package fr.uge.but.schtroumpf;

import module java.base;

import fr.uge.but.schtroumpf.controller.console.AppController;
import fr.uge.but.schtroumpf.view.AppWindow;
import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.Logger.LoggerFlag;;

public class Main {
	public static void main(String[] args) {
		// init logger
		List<LoggerFlag> flags = List.of(
				LoggerFlag.DEBUG,
				LoggerFlag.ERROR,
				LoggerFlag.TRACE
		);
		Logger.Init(flags);
		
		// test logger
		Logger.LogDebug("hello");
		
		// launch fxml window
		AppWindow fxmlApp = new AppWindow();
		fxmlApp.launch();
	}
}
