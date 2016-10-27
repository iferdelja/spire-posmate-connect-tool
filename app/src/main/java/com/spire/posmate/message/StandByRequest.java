package com.spire.posmate.message;

public class StandByRequest extends RequestMessage {

    // ASCII 13
    public StandByRequest() {
        setIdentifier(new byte[]{0x31, 0x33});
    }
}
