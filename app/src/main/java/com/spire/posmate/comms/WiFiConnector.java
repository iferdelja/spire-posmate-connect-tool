package com.spire.posmate.comms;

import java.io.IOException;

/**
 * For future use.
 */
public class WiFiConnector extends AbstractConnector {
	@Override
	public boolean isConnected() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void connect() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disconnect() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void write(int oneByte) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void write(byte[] bytes) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int read() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int read(byte[] buffer, int start, int byteCount) throws IOException {
		throw new UnsupportedOperationException();
	}
}
