package com.spire.posmate.message;

public class StartProcessRequest extends RequestMessage {

    // ASCII 41
    public StartProcessRequest() {
        setIdentifier(new byte[]{0x34, 0x31});
    }
}
