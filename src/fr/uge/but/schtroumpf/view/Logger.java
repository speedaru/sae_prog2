package fr.uge.but.schtroumpf.view;

import module java.base;

public class Logger {
	// global logging level
	private static final HashSet<LoggerFlag> logLevel = new HashSet<LoggerFlag>();
	
	public static void Init(List<LoggerFlag> flags) {
		for (LoggerFlag flag : flags) {
			Logger.logLevel.add(flag);
		}
	}
	
	public static boolean flagSet(LoggerFlag flag) {
		return logLevel.contains(flag);
	}
	
	public static void LogDebug(String message, Object... args) { Log(LoggerFlag.DEBUG, String.format(message, args)); }
	public static void LogWarn(String message, Object... args) { Log(LoggerFlag.WARNING, String.format(message, args)); }
	public static void LogTrace(String message, Object... args) { Log(LoggerFlag.TRACE, String.format(message, args)); }
	public static void LogError(String message, Object... args) { Log(LoggerFlag.ERROR, String.format(message, args)); }

	// main logging function, can change to log to file maybe
	private static void Log(LoggerFlag level, String message) {
		Objects.requireNonNull(logLevel);
		if (flagSet(level)) {
			IO.println(String.format("%s : %s", level.name(), message));
		}
	}

	public enum LoggerFlag {
		DEBUG,
		WARNING,
		TRACE,
		ERROR;
	}
}
