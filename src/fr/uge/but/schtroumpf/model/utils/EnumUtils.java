package fr.uge.but.schtroumpf.model.utils;

import fr.uge.but.schtroumpf.model.types.CodeEnum;

public class EnumUtils {
	public static <E extends Enum<E> & CodeEnum> E fromCode(Class<E> enumClass, int code) {
		for (E value : enumClass.getEnumConstants()) {
			if (value.getCode() == code) {
				return value;
			}
		}

		throw new IllegalArgumentException("unknown code: " + code);
	}
}
