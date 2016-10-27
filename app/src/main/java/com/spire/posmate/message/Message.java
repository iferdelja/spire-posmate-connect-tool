package com.spire.posmate.message;

import android.util.Log;

import com.spire.posmate.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Message {
    private byte[] identifier;
    private byte sequenceNumber;
    private byte[] data;

    protected Message() {
    }

    public byte[] getIdentifier() {
        return identifier;
    }

    public void setIdentifier(byte[] identifier) {
        this.identifier = identifier;
    }

    public byte getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(byte sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            writeDataPrefix(stream);
        } catch (IOException e) {
            Log.e(Logger.LOG_TAG, e.getMessage());
        }

        byte[] dataField = getData();
        if (dataField != null && dataField.length > 0) {
//            byte lc = (byte) dataField.length;
//
//            stream.write(lc);
            try {
                stream.write(dataField);
            } catch (IOException e) {
                Log.e(Logger.LOG_TAG, e.getMessage());
            }
        }

        return stream.toByteArray();
    }

    protected void writeDataPrefix(ByteArrayOutputStream stream) throws IOException {

    }

    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        int lc = data != null ? data.length : 0;

        return String.format("%s: SEQ: %02X, DATA: [LC: %d]", name, sequenceNumber, lc);
    }

    public static Message parseMessage(byte[] messageBytes) {
        Message message = new Message();
        message.setIdentifier(new byte[]{messageBytes[0], messageBytes[1]});
        message.setSequenceNumber(messageBytes[2]);

        // TODO Improve on this by reusing original bytes!
        message.setData(Arrays.copyOfRange(messageBytes, 3, messageBytes.length));

        return message;
    }

    public static int getMessageIdentifier(byte[] messageIdentifier) {
        return ((int) messageIdentifier[0] - 48) * 10 + ((int) messageIdentifier[1] - 48);
    }

    public int getResponse() {
        return ((int) data[0] - 48);
    }

    public static String parseField(byte[] field) {
        StringBuilder sb = new StringBuilder();
        for (byte b : field) {
            sb.append((char)b);
        }
        return sb.toString();
    }

    public static String responseDescriptorForId(int id) {
        switch (id) {
            case 0:
                return "Success";
            case 1:
                return "Failed to process request.";
            case 2:
                return "Require suplementary information";
            case 3:
                return "Abort operation";
            case 4:
                return "Require suplementary MAC generation";
            case 5:
                return "Require suplementary MAC verification";
            default:
                return "Unknown response code " + id;
        }
    }
}
