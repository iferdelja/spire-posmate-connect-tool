package com.spire.posmate.comms;

import com.spire.posmate.error.NotConnectedException;

import java.io.IOException;

/**
 * Interface representing the methods required by Client. A Connector
 * implementation/instance is expected to handle communication with a
 * single MPI terminal.
 */
public interface Connector {
	/**
	 * Returns the "connected" status.
	 * 
	 * @return True if "connected", otherwise false.
	 */
	public boolean isConnected();
	
	/**
	 * Connects to the terminal. If there is an error during the connect
	 * operation exceptions are expected. After a successful call to
	 * {@link #connect()}, {@link #isConnected()} should return true.
	 * 
	 * @throws IOException When something goes wrong while connecting.
	 */
	public void connect() throws IOException;
	
	/**
	 * Disconnects from the terminal. After a successful call to
	 * {@link #disconnect()} {@link #isConnected()} will return false.
	 * 
	 * @throws IOException When something goes wrong while disconnecting.
	 */
	public void disconnect() throws IOException;
	
	/**
	 * Sends the 'data' byte array to the connected terminal. Returns the
	 * number of bytes sent. An exception may be raised if the connection
	 * fails or not all the data can be sent for some reason. It is expected
	 * that the return value should equal the length of data.
	 * 
	 * @param data Data to be sent.
	 * @return Number of bytes sent.
	 * 
	 * @throws NotConnectedException When attempting to send while not connected.
	 * @throws IOException When something goes wrong during sending.
	 */
	public int send(byte[] data) throws NotConnectedException, IOException;
	
	/**
	 * Sends the lowest byte of {@code oneByte} to the terminal if the overall
	 * {@code oneByte} value is between 0 and 255.
	 * 
	 * @param oneByte The byte to be sent.
	 * @return Number of bytes sent (i.e. number 1).
	 * 
	 * @throws NotConnectedException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public int send(int oneByte) throws NotConnectedException, IOException, IllegalArgumentException;
	
	/**
	 * Receive up to {@code byteCount} data from the terminal and return it as a
	 * byte array. {@link #receive(int)} will wait for {@code byteCount} to be
	 * captured prior and to returning the result.
	 * 
	 * @param byteCount Byte count to be received.
	 * @return The received byte array.
	 * 
	 * @throws NotConnectedException When attempting to receive while not connected.
	 * @throws IOException When something goes wrong during receiving.
	 */
	public byte[] receive(int byteCount) throws NotConnectedException, IOException;
	
	/**
	 * Receive a single byte from the terminal and returns it as an integer in the
	 * range from 0 to 255.
	 * 
	 * @return The byte received.
	 * 
	 * @throws NotConnectedException
	 * @throws IOException
	 */
	public int receive() throws NotConnectedException, IOException;
	
	/**
	 * <b>[FOR FUTURE USE]</b>
	 * Sets the maximum period a {@link #send(byte[])} or {@link #receive(int)} call
	 * should wait in blocking mode.  It is expected that timeout=None sets blocking
	 * with no timeouts, timeout=0 set non-blocking mode, and timeout>0 is a real
	 * value where 0.001 is understood as 1 millisecond, and any multiple thereof
	 * is acceptable.<br>
	 * <br>
	 * Note: Presently {@link #setTimeout(float)} is always called with 0 (non-blocking).
	 * 
	 * @param timeout Number of seconds to wait.
	 */
	public void setTimeout(float timeout);

	public int available() throws IOException;
}
