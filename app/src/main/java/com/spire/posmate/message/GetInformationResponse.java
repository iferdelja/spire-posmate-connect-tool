package com.spire.posmate.message;

import java.util.Arrays;

public class GetInformationResponse extends ResponseMessage {

    public String getSerialNumber() {
        byte[] serialNumber = Arrays.copyOf(getData(), 8);
        return Message.parseField(serialNumber);
    }

    public String getVersionNumber() {
        byte[] versionNumber = Arrays.copyOfRange(getData(), 8, getData().length);
        return Message.parseField(versionNumber);
    }

    @Override
    public String toString() {
        return String.format("SEQ: %02X, SERIALNO: %s, VER: %s", getSequenceNumber(), getSerialNumber(), getVersionNumber());
    }

}
