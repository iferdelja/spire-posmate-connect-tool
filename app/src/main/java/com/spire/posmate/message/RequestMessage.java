package com.spire.posmate.message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RequestMessage extends Message {

    public RequestMessage() {
        super();
    }

    @Override
    protected void writeDataPrefix(ByteArrayOutputStream stream) throws IOException{
        stream.write(getIdentifier());
        stream.write(new byte[] {getSequenceNumber()});
    }
}
