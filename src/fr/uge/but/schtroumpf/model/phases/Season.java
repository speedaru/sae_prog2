package fr.uge.but.schtroumpf.model.phases;

public enum Season {
	SPRING(1, 3, 5, "Praiontemps"),
	SUMMER(2, 6, 8, "Ete"),
	AUTUMN(3, 9, 11, "Autaumn"),
	WINTER(4, 12, 2, "Hyver");
	
	private int code;
	private int startMonth;
	private int endMonth;
	private String name;
	
	Season(int code, int startMonth, int endMonth, String name) {
		this.code = code;
		this.startMonth = startMonth;
		this.endMonth = endMonth;
		this.name = name;
	}
	
	public int getCode() { return code; }
	public int getStartMonth() { return startMonth; }
	public int getEndMonth() { return endMonth; }
	public String getName() { return name; }
	
	public static Season getSeason(int monthNumber) {
		for (Season season : Season.values()) {
			boolean inRange = false;
			int startMonth = season.getStartMonth();
			int endMonth = season.getEndMonth();
			
			// check if monthNumber is in range
			if (startMonth <= endMonth) {
				inRange = startMonth <= monthNumber && monthNumber <= endMonth;
			}
			else { // start month is in previous year
				inRange = startMonth <= monthNumber || monthNumber <= endMonth;
			}
			
			if (inRange) {
				return season;
			}
		}
		throw new IllegalStateException("invalid month number: " + monthNumber);
	}
}
