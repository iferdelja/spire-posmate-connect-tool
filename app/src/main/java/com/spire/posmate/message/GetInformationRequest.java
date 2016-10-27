package com.spire.posmate.message;

public class GetInformationRequest extends RequestMessage {

    // ASCII 15
    // 2 bytes
    // Hex: 0x31 0x35
    // Dec: 49 53
    public GetInformationRequest() {
        setIdentifier(new byte[]{0x31, 0x35});
    }
}
