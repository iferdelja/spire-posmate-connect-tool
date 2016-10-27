package com.spire.posmate.error;

public class DeviceNotFound extends Exception {
	public DeviceNotFound() {
		super();
	}
	
	public DeviceNotFound(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
