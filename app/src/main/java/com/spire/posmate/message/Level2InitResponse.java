package com.spire.posmate.message;

import java.util.Arrays;

/**
 * TODO: Warning - Implementation must be updated to consider that reader capabilities and
 * secure mode are optional fields.
 */
public class Level2InitResponse extends ResponseMessage {

    public byte[] getReaderCapabilities() {
        return Arrays.copyOfRange(getData(), 0, 2);
    }

    public byte getSecureMode() {
        return getData()[getData().length - 1];
    }

    @Override
    public String toString() {
        return String.format("%s ID:%s SEQ: %02X, RESP:%02X, READERCAPA:%s, SECMODE:%02X",
                this.getClass().getName(),
                Message.parseField(getIdentifier()),
                getSequenceNumber(),
                getResponse(),
                Message.parseField(getReaderCapabilities()),
                getSecureMode());
    }

}
