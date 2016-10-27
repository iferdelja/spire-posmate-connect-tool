package com.spire.posmate.utils;

import android.util.Log;

import com.spire.posmate.Logger;

public abstract class SystemUtils {
	private static String[] hexValues;
	
	public static String toHex(byte oneByte) {
		String[] hexValues = getHexValues();
		return hexValues[oneByte & 0xFF];
	}
	
	public static void appendHexValues(StringBuilder sb, byte[] bytes, String delimiter) {
		if (bytes != null) {
			String[] hexValues = getHexValues();
			
			for (byte b : bytes) {
				sb.append(hexValues[b & 0xFF]);
				sb.append(delimiter);
			}
		}
	}
	
	private static String[] getHexValues() {
		if (hexValues == null) {
			Log.d(Logger.LOG_TAG, "SystemUtils: Creating hex values...");
			
			hexValues = new String[256];
			
			for (int i = 0; i < 256; i++) {
				hexValues[i] = String.format("%02X", i);
			}
		}
		
		return hexValues;
	}

	public static void threadSleepNoInterrupt(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
