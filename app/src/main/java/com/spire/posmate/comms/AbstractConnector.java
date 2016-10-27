package com.spire.posmate.comms;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.error.NotConnectedException;
import com.spire.posmate.utils.SystemUtils;

import java.io.IOException;

public abstract class AbstractConnector implements Connector {
	@Override
	public abstract boolean isConnected();

	@Override
	public abstract void connect() throws IOException;

	@Override
	public abstract void disconnect() throws IOException;

	@Override
	public int send(byte[] packet) throws NotConnectedException, IOException {
		if (!isConnected()) {
			throw new NotConnectedException();
		}
		
		write(packet);
		
		logDebug("Bytes sent: ", packet);
		
		return packet.length;
	}
	
	@Override
	public int send(int oneByte) throws NotConnectedException, IOException, IllegalArgumentException {
		if (oneByte < 0 || oneByte > 0xFF) {
			throw new IllegalArgumentException("Unsupported value: " + oneByte);
		}
		
		write(oneByte);
		
		logDebug("Byte sent: ", new byte[] {(byte) oneByte});
		
		return 1;
	}

	@Override
	public byte[] receive(int byteCount) throws NotConnectedException, IOException {
		if (!isConnected()) {
			throw new NotConnectedException();
		}
			
		byte[] buffer = new byte[byteCount];
		int bytesRead = 0;
		
		do {
			bytesRead += read(buffer, bytesRead, byteCount - bytesRead);
		} while (bytesRead < byteCount);
		
		logDebug("Bytes received: ", buffer);
		
		return buffer;
	}
	
	@Override
	public int receive() throws NotConnectedException, IOException {
		if (!isConnected()) {
			throw new NotConnectedException();
		}
		
		int oneByte = read();
		
		logDebug("Byte received: ", new byte[] {(byte) oneByte});
		
		if (oneByte == -1) {
			Log.w(Logger.LOG_TAG, getClass().getSimpleName() + ": Negative value returned.");
			throw new NotConnectedException();
		}
		
		return oneByte;
	}
	
	protected void logDebug(String prefix, byte[] bytes) {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName() + ": " + prefix);
		
		SystemUtils.appendHexValues(sb, bytes, " ");
		
		Log.d(Logger.LOG_TAG, sb.toString());
	}

	@Override
	public int available() throws IOException {
		return 0;
	}

	protected abstract void write(int oneByte) throws IOException;
	protected abstract void write(byte[] bytes) throws IOException;
	
	protected abstract int read() throws IOException;
	protected abstract int read(byte[] buffer, int start, int byteCount) throws IOException;
	
	@Override
	public void setTimeout(float timeout) {
		throw new UnsupportedOperationException();
	}
}
