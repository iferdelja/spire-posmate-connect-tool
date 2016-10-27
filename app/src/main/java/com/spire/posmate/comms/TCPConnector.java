package com.spire.posmate.comms;

import android.util.Log;

import com.spire.posmate.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnector extends AbstractConnector {
	private String destination;
	private int portNumber;
	private Socket socket;
	
	public TCPConnector(String destination, int portNumber) {
		this.destination = destination;
		this.portNumber = portNumber;
	}

	@Override
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	@Override
	public void connect() throws IOException {
		try {
			socket = new Socket(destination, portNumber);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.e(Logger.LOG_TAG, e.getMessage());
		}
	}

	@Override
	public synchronized void disconnect() throws IOException {
		if (socket != null) {
			socket.getOutputStream().close();
			socket.getInputStream().close();
			socket.close();
			socket = null;
		}
	}
	
	@Override
	protected void write(int oneByte) throws IOException {
		socket.getOutputStream().write(oneByte);
		socket.getOutputStream().flush();
	}

	@Override
	protected void write(byte[] bytes) throws IOException {
		socket.getOutputStream().write(bytes);
		socket.getOutputStream().flush();
	}

	@Override
	protected int read() throws IOException {
		return socket.getInputStream().read();
	}

	@Override
	protected int read(byte[] buffer, int start, int byteCount) throws IOException {
		return socket.getInputStream().read(buffer, start, byteCount);
	}
}
