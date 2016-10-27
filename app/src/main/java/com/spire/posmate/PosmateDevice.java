package com.spire.posmate;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.spire.posmate.comms.BluetoothConnector;
import com.spire.posmate.comms.Connector;
import com.spire.posmate.error.NotConnectedException;
import com.spire.posmate.message.Message;
import com.spire.posmate.message.ResponseMessage;
import com.spire.posmate.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Handles communication with a Posmate terminal.
 */
public class PosmateDevice {
    private static final int CONNECT_RETRY_TIMEOUT = 100;

    private static final int STATE_RUNNING = 0x0001;
    private static final int STATE_DISCONNECTED = 0x0002;

    private volatile int state;

    private Connector connector;

    public static PosmateDevice create() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Log.e(Logger.LOG_TAG, "No bt adapter.");
            return null;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Log.e(Logger.LOG_TAG, "Bt adapter not enabled.");
            return null;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices == null || bondedDevices.size() == 0) {
            Log.e(Logger.LOG_TAG, "No bonded bt devices.");
            return null;
        }

        PosmateDevice posmateDevice = null;
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            if (bluetoothDevice.getName().toLowerCase(Locale.US).contains("posmate")) {
                Connector connector = new BluetoothConnector(
                        BluetoothAdapter.getDefaultAdapter(), bluetoothDevice);
                posmateDevice = new PosmateDevice(connector);
                break;
            }
        }

        return posmateDevice;
    }

    public PosmateDevice(Connector connector) {
        this.connector = connector;
    }

    public boolean connect() {
        this.state = STATE_RUNNING;
        int maxAttempts = 1000;

        boolean connected = isConnected();

        if (!connected) {
            int attemptCount = 1;

            while (attemptCount <= maxAttempts && !isDisconnected()) {
                Log.d(Logger.LOG_TAG, String.format("%s: Connection attempt #" + attemptCount, this));

                try {
                    Log.d(Logger.LOG_TAG, String.format("%s: Connecting...", this));
                    connector.connect();
                    connected = true;
                    Log.d(Logger.LOG_TAG, String.format("%s: Connected.", this));
                } catch (IOException e) {
                    Log.e(Logger.LOG_TAG, e.toString());
                }

                if (connected || isDisconnected()) {
                    break;
                }

                attemptCount++;
                Log.i(Logger.LOG_TAG, String.format("%s: Attempt failed. Retrying in %d ms...", this, CONNECT_RETRY_TIMEOUT));

                try {
                    Thread.sleep(CONNECT_RETRY_TIMEOUT);
                } catch (InterruptedException e) {
                    Log.e(Logger.LOG_TAG, e.toString());
                }
            }
        } else {
            Log.d(Logger.LOG_TAG, String.format("%s: Already connected.", this));
        }

        return connected;
    }

    public void disconnect() {
        this.state = STATE_DISCONNECTED;
        if (connector != null) {
            try {
                Log.d(Logger.LOG_TAG, String.format("%s: Disconnecting...", this));
                connector.disconnect();
                Log.d(Logger.LOG_TAG, String.format("%s: Disconnected.", this));
            } catch (IOException e) {
                Log.e(Logger.LOG_TAG, e.toString());
            }
        } else {
            Log.d(Logger.LOG_TAG, String.format("%s: Already disconnected.", this));
        }
    }

    public boolean isDisconnected() {
        return ((state & STATE_DISCONNECTED) != 0);
    }

    public boolean isConnected() {
        return connector.isConnected();
    }

    /**
     * Sends an message to the terminal.
     * Recipient must respond either with a ACK (if LRC is valid) or a NAK (if LRC not valid).
     * If NAK is received, message must be sent again.
     *
     * @param message The message to be sent.
     * @return True if successfully sent, false otherwise.
     */
    public boolean sendMessage(Message message) {
        Log.d(Logger.LOG_TAG, String.format("%s: Sending message: " + message, this));

        Packet packet = new Packet(message);

        final int MAX_ATTEMPTS = 3;
        int executedAttempts = 0;
        boolean messageSent = false;

        while (!messageSent && executedAttempts < MAX_ATTEMPTS) {
            if (sendBytes(packet.toByteArray())) {
                Log.d(Logger.LOG_TAG, String.format("Sent message packet "));

                try {
                    //Log.d(Logger.LOG_TAG, String.format("Will receive, available: " + connector.available()));
                    int response = connector.receive();
                    Log.d(Logger.LOG_TAG, String.format("Received response to packet: " + response));

                    switch (response) {
                        case Packet.ACK:
                            messageSent = true;
                            break;
                    }
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!messageSent) {
                executedAttempts++;
            }
        }
        return messageSent;
    }

    public Message receiveMessage() {
        Message message = null;
        Packet packet = null;

        ByteArrayOutputStream information = new ByteArrayOutputStream();

        packet = receivePacket();

        if (packet != null) {
            Log.d(Logger.LOG_TAG, String.format("%s: Packet valid: %b", this, packet.isValid()));

            if (!packet.isValid()) {
                Log.w(Logger.LOG_TAG, String.format("%s: The packet is invalid, hence returning null...", this));
                information = null;
            } else {
                sendBytes(new byte[]{Packet.ACK});
                try {
                    information.write(packet.getInformation());
                } catch (IOException e) {
                    Log.w(Logger.LOG_TAG, e.getMessage());
                }
            }


        } else {
            Log.i(Logger.LOG_TAG, String.format("%s: Response packet is null, hence returning null.", this));
            information = null;
        }

        if (information != null) {
            message = Message.parseMessage(information.toByteArray());
            Log.d(Logger.LOG_TAG, String.format("%s: Response received", this));
        }

        return message;
    }

    /**
     * Sends the prepared byte array to the connected device.
     *
     * @param bytes Bytes to be sent.
     * @return True if bytes were successfully sent, false otherwise.
     */
    public boolean sendBytes(byte[] bytes) {
        boolean sent = false;

        Log.d(Logger.LOG_TAG, String.format("%s: Sending bytes...", this));

        if (isConnected()) {
            try {
                connector.send(bytes);

                Log.d(Logger.LOG_TAG, String.format("%s: Bytes sent: " + bytes.length, this));

                sent = true;
            } catch (IOException e) {
                Log.e(Logger.LOG_TAG, e.toString());
            } catch (NotConnectedException e) {
                Log.e(Logger.LOG_TAG, e.toString());
            }
        } else {
            Log.w(Logger.LOG_TAG, String.format("%s: Cannot send, not connected to a terminal.", this));
        }

        //threadSleepNoInterrupt(1000);
        return sent;
    }

    /**
     * Receives a data packet from the device. If a problem occurs or the packet is invalid,
     * null is returned.
     *
     * @return The response packet or null.
     */
    private Packet receivePacket() {
        Packet packet = null;

        byte[] buffer = receivePacketBytes();

        if (buffer != null) {
            packet = new Packet(buffer);
        }

        return packet;
    }

    /**
     * Reads packet bytes from device's data stream.
     *
     * @return Bytes read from the stream or null.
     */
    public byte[] receivePacketBytes() {
        byte[] result = null;

        Log.d(Logger.LOG_TAG, String.format("%s: Receiving bytes...", this));

        if (isConnected()) {
            try {
                while (true) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    int packetByte;
                    do {
                        packetByte = connector.receive();
                        stream.write(packetByte);
                    } while (!Packet.isEndOfPacket((byte) packetByte));

                    // Get the LRC
                    stream.write(connector.receive());

                    result = stream.toByteArray();

                    Log.d(Logger.LOG_TAG, String.format("%s: Bytes received: " + result.length, this));
                    break;
                }
            } catch (IOException e) {
                Log.e(Logger.LOG_TAG, e.toString());
            } catch (NotConnectedException e) {
                Log.e(Logger.LOG_TAG, e.toString());
            }
        } else {
            Log.w(Logger.LOG_TAG, String.format("%s: Cannot receive, not connected to a terminal.", this));
        }

        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s<%s>", Thread.currentThread().getName(), getClass().getSimpleName(),
                connector.getClass().getSimpleName());
    }
}
