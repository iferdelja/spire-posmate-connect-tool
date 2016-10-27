package com.spire.posmate.message;

public class Level2InitRequest extends RequestMessage {

    // ASCII 12
    public Level2InitRequest() {
        setIdentifier(new byte[]{0x31, 0x32});
    }
}
