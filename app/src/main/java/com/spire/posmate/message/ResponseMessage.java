package com.spire.posmate.message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseMessage extends Message {

    private byte response;

    public ResponseMessage() {
        super();
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(byte response) {
        this.response = response;
    }

    @Override
    protected void writeDataPrefix(ByteArrayOutputStream stream) throws IOException {
        stream.write(getIdentifier());
        stream.write(new byte[] {getSequenceNumber()});
        stream.write(getResponse());
    }
}
