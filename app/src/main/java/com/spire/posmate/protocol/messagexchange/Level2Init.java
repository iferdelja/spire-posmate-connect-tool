package com.spire.posmate.protocol.messagexchange;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Level2InitRequest;
import com.spire.posmate.message.Message;
import com.spire.posmate.packet.Packet;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;
import com.spire.posmate.utils.SystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Level2Init extends MessageExchange {

    Message receivedResponse;

    public Level2Init(MessageExchangeSupervisor supervisor, Logger logger) {
        super(supervisor, logger);
        mei = MEI.LEVEL2_INIT;
    }

    public void startExchangeWithEvent(MessageExchangeEvent event) {
        nextEvent = event;
        while (nextEvent != MessageExchangeEvent.SUCCESS && nextEvent != MessageExchangeEvent.FAILURE) {
            //logger.log("MEI " + mei.getDescriptor() + " will run event " + nextEvent.name());
            switch (nextEvent) {
                case SEND_REQUEST:
                    logOutgoingMessage("");
                    nextEvent = supervisor.sendMessage(buildRequest()) ?
                            MessageExchangeEvent.RECEIVE_RESPONSE : MessageExchangeEvent.FAILURE;
                    break;
                case RECEIVE_RESPONSE:
                    receivedResponse = supervisor.receiveMessage();
                    nextEvent = receivedResponse != null ?
                            MessageExchangeEvent.PROCESS_RESPONSE : MessageExchangeEvent.FAILURE;
                    break;
                case PROCESS_RESPONSE:
                    nextEvent = processResponse(receivedResponse) ?
                            MessageExchangeEvent.SUCCESS : MessageExchangeEvent.FAILURE;
                    break;
            }
        }

        supervisor.messageExchangeDone(mei, nextEvent == MessageExchangeEvent.SUCCESS);
    }

    private Message buildRequest() {
        Message message = new Level2InitRequest();
        message.setSequenceNumber((byte) 49);
        message.setData(prepareDummyLevel2InitMessageData());
        return message;
    }

    private boolean processResponse(Message responseMessage) {
//
//
//        return String.format("%s ID:%s SEQ: %02X, RESP:%02X, READERCAPA:%s, SECMODE:%02X",
//                this.getClass().getName(),
//                Message.parseField(getIdentifier()),
//                getSequenceNumber(),
//                getResponse(),
//                Message.parseField(getReaderCapabilities()),
//                getSecureMode());

        logIncomingMessage(Message.responseDescriptorForId(responseMessage.getResponse()));
        return true;
    }

    private static byte[] prepareDummyLevel2InitMessageData() {
        byte[] out = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write("36408082".getBytes("US-ASCII"));
            baos.write("22".getBytes("US-ASCII"));
            baos.write("191".getBytes("US-ASCII"));
            baos.write("E0F8C8".getBytes("US-ASCII"));
            baos.write("E000F0A001".getBytes("US-ASCII"));
            baos.write("191".getBytes("US-ASCII"));
            baos.write("2".getBytes("US-ASCII"));
            baos.write("7299".getBytes("US-ASCII"));
            baos.write("Live Merchant".getBytes("US-ASCII"));
            baos.write(Packet.FS);
            baos.write("R".getBytes("US-ASCII"));
            baos.write(Packet.FS);
            baos.write(Packet.FS);
            baos.write(Packet.FS);
            baos.write(Packet.FS);
            baos.write(Packet.FS);
            baos.write("hr".getBytes("US-ASCII"));

            out = baos.toByteArray();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return out;
    }
}
