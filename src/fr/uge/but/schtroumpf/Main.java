package fr.uge.but.schtroumpf;

import module java.base;

import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.LoggerFlag;

public class Main {
	public static void main(String[] args) {
		// init logger
		List<LoggerFlag> flags = List.of(
				LoggerFlag.Debug,
				LoggerFlag.Error,
				LoggerFlag.Trace
		);
		Logger.Init(flags);
		
		Logger.LogError("hello");
	}
}
