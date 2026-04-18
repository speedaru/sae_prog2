package fr.uge.but.schtroumpf.view;

import module java.base;

public class Logger {
	// global loggin level
	private static HashSet<LoggerFlag> logLevel = new HashSet<LoggerFlag>();
	
	public static void Init(List<LoggerFlag> flags) {
		for (LoggerFlag flag : flags) {
			Logger.logLevel.add(flag);
		}
	}
	
	public static boolean flagSet(LoggerFlag flag) {
		return logLevel.contains(flag);
	}
	
	public static void LogDebug(String message) { Log(LoggerFlag.Debug, message); }
	public static void LogTrace(String message) { Log(LoggerFlag.Trace, message); }
	public static void LogError(String message) { Log(LoggerFlag.Error, message); }

	// main logging function, can change to log to file maybe
	private static void Log(LoggerFlag level, String message) {
		Objects.requireNonNull(logLevel);
		if (flagSet(level)) {
			IO.println(String.format("%s : %s", level.getDescription(), message));
		}
	}
}
