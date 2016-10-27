package com.spire.posmate.utils;

public class BCD {
	public static byte[] pack(long value) {
		return pack(value, null);
	}
	
	public static byte[] pack(long value, byte[] result) {
		long temp = value;
		
		if (result == null) {
			int byteCount = 0;
			
			while (temp > 0) {
				byteCount++;
				temp /= 100;
			}
			
			temp = value;
			result = new byte[byteCount];
		}
		
		for (int i = result.length - 1; i >= 0; i--) {
			int digits = (int) (temp % 100);
			
			byte upper = (byte) ((digits / 10) << 4);
			byte lower = (byte) (digits % 10);
			
			result[i] = (byte) (upper | lower);
			
			temp /= 100;
		}
		
		return result;
	}
	
	public static byte[] pack(int[] values) {
		byte[] result = new byte[values.length];
		
		for (int i = 0; i < values.length; i++) {
			int value = values[i] % 100;
			
			byte upper = (byte) ((value / 10) << 4);
			byte lower = (byte) (value % 10);
			
			result[i] = (byte) (upper | lower);
		}
		
		return result;
	}
	
	public static int[] unpack(byte[] values) {
		int[] result = new int[values.length];
		
		for (byte i = 0; i < values.length; i++) {
			byte value = values[i];
			
			byte upper = (byte) ((value & (byte) 0xF0) >>> 4);
			byte lower = (byte) (value & (byte) 0x0F);
			
			result[i] = 10*upper + lower;
		}
		
		return result;
	}
}
