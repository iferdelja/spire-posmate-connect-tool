package com.spire.posmate.comms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnector extends AbstractConnector {
	// a well-known SPP UUID
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	
	public BluetoothConnector(BluetoothAdapter adapter, BluetoothDevice device) {
		this.adapter = adapter;
		this.device = device;
	}

	@Override
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	@Override
	public void connect() throws IOException {
		adapter.cancelDiscovery();
		socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
		socket.connect();
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
	public int available() throws IOException {
		return socket.getInputStream().available();
	}

	@Override
	protected int read(byte[] buffer, int start, int byteCount) throws IOException {
		return socket.getInputStream().read(buffer, start, byteCount);
	}
}
