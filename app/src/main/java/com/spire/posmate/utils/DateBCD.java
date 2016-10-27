package com.spire.posmate.utils;

import java.util.Calendar;

public class DateBCD extends BCD {
	public static byte[] packDate(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR) % 100;
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		return pack(new int[] {year, month, day});
	}
	
	public static byte[] packTime(Calendar calendar) {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		
		return pack(new int[] {hour, minute, second});
	}
	
	public static Calendar unpackDateTime(byte[] dateValues, byte[] timeValues) {
		Calendar calendar = unpackDate(dateValues);
		Calendar time = unpackTime(timeValues);
		
		calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, time.get(Calendar.SECOND));
		
		return calendar;
	}
	
	public static Calendar unpackDate(byte[] values) {
		int[] result = unpack(values);
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		calendar.set(Calendar.YEAR, result[0] + 2000);
		calendar.set(Calendar.MONTH, result[1] - 1);
		calendar.set(Calendar.DAY_OF_MONTH, result[2]);
		
		return calendar;
	}
	
	public static Calendar unpackTime(byte[] values) {
		int[] result = unpack(values);
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		calendar.set(Calendar.HOUR_OF_DAY, result[0]);
		calendar.set(Calendar.MINUTE, result[1]);
		calendar.set(Calendar.SECOND, result[2]);
		
		return calendar;
	}
}
