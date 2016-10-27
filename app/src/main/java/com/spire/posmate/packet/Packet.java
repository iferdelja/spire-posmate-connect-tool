package com.spire.posmate.packet;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Spire Posmate packet format
 *
 * STX | MESSAGE | ETX | LRC
 *
 * STX = 1 byte indicating start of the message
 * MESSAGE = n bytes of message content
 * ETX = 1 byte indicating end of the message
 * LRC = 1 byte longitudinal redundancy check - XOR(MESSAGE,ETX)
 */
public class Packet {

	public static final byte STX = 0x02;
	public static final byte ETX = 0x03;
	public static final byte ENQ = 0x05;
	public static final byte ACK = 0x06;
	public static final byte NAK = 0x15;
	public static final byte FS = 0x1C; // Dec:28

	protected static final int PROLOG_LENGTH = 1;
	protected static final int EPILOG_LENGTH = 2;

	private byte[] prolog;
	private byte[] information;
	private byte[] epilog;

	public Packet(Message message) {
		setInformation(message.toByteArray());
		constructProlog();
		constructEpilog();
	}

	public Packet(byte[] packet) {
		if (packet == null) {
			throw new IllegalArgumentException("Response byte array cannot be null.");
		}

		splitInputArray(packet);
	}

	private void splitInputArray(byte[] packet) {
		byte[] prolog = new byte[PROLOG_LENGTH];
		byte[] information = new byte[packet.length - PROLOG_LENGTH - EPILOG_LENGTH];
		byte[] epilog = new byte[EPILOG_LENGTH];

		ByteArrayInputStream packetStream = new ByteArrayInputStream(packet);

		try {
			packetStream.read(prolog);
			packetStream.read(information);
			packetStream.read(epilog);
		} catch (IOException e) {
			Log.e(Logger.LOG_TAG, e.getMessage());
		}

		setProlog(prolog);
		setInformation(information);
		setEpilog(epilog);
	}

	protected byte[] getProlog() {
		return prolog;
	}
	
	protected void setProlog(byte[] prolog) {
		if (prolog.length != PROLOG_LENGTH) {
			throw new IllegalArgumentException("Illegal prolog length: " + prolog.length);
		}
		
		this.prolog = prolog;
	}
	
	public byte[] getInformation() {
		return information;
	}
	
	protected void setInformation(byte[] information) {
		if (information == null) {
			throw new IllegalArgumentException("Illegal information value: " + information);
		}
		
		this.information = information;
	}
	
	protected byte[] getEpilog() {
		return epilog;
	}
	
	protected void setEpilog(byte[] epilog) {
		if (epilog.length != EPILOG_LENGTH) {
			throw new IllegalArgumentException("Illegal epilog length: " + epilog.length);
		}
		
		this.epilog = epilog;
	}
	
	protected byte calculateLrc() {
		byte lrc = 0;

		for (byte b : getInformation()) {
			lrc ^= b;
		}

		lrc ^= ETX;

		return lrc;
	}
	
	protected int size() {
		return getProlog().length + getInformation().length + getEpilog().length;
	}
	
	/**
	 * Constructs a byte array for the packet.
	 * 
	 * @return Packet's byte array representation.
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(size());
		
		try {
			stream.write(getProlog());
			stream.write(getInformation());
			stream.write(getEpilog());
		} catch (IOException e) {
			Log.e(Logger.LOG_TAG, e.getMessage());
		}
		
		return stream.toByteArray();
	}

	private void constructProlog() {
		setProlog(new byte[] {STX});
	}

	private void constructEpilog() {
		byte lrc = calculateLrc();
		setEpilog(new byte[] {ETX, lrc});
	}

	public boolean isValid() {
		boolean isValid = true;
		isValid &= getProlog().length == 1;
		isValid &= getProlog()[0] == STX;
		isValid &= isLrcValid();
		return isValid;
	}

	private boolean isPrologValid() {
		return getProlog().length == 1 && getProlog()[0] == STX;
	}

	private boolean isEpilogValid() {
		return getEpilog().length == 2 && getEpilog()[0] == ETX;
	}

	private byte getLrc() {
		return getEpilog()[1];
	}

	private boolean isLrcValid() {
		byte lrc = calculateLrc();
		boolean isValid = (getLrc() == lrc);

		if (!isValid) {
			String message = String.format("%02X (recv) != %02X (calc)", getLrc(), lrc);
			logValidationError("LRC", message);
		}

		return isValid;
	}

	public static boolean isEndOfPacket(int packetByte) {
		return packetByte == ETX;
	}

	private void logValidationError(String fieldName, byte fieldValue) {
		logValidationError(fieldName, String.format("%02X", fieldValue));
	}

	private void logValidationError(String fieldName, String fieldValue) {
		Log.e(Logger.LOG_TAG, "ResponsePacket: " + fieldName + " not valid: " + fieldValue);
	}

}
