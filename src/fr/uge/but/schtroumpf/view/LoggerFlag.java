package fr.uge.but.schtroumpf.view;

import module java.base;

public enum LoggerFlag {
	// constants
	Debug(1, "DEBUG"),
	Trace(2, "TRACE"),
	Error(3, "ERROR");
	
	private final int code;
	private final String description;
	
	LoggerFlag(int code, String description) {
		this.code = code;
		this.description = Objects.requireNonNull(description);
	}
	
	public int getCode() { return code; }
	public String getDescription() { return description; }
}
