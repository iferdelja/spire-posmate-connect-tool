package com.spire.posmate.protocol.messagexchange;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Level2InitRequest;
import com.spire.posmate.message.Message;
import com.spire.posmate.message.StartProcessRequest;
import com.spire.posmate.packet.Packet;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class StartProcess extends MessageExchange {

    Message receivedResponse;

    public StartProcess(MessageExchangeSupervisor supervisor, Logger logger) {
        super(supervisor, logger);
        mei = MEI.START_PROCESS;
    }

    public void startExchangeWithEvent(MessageExchangeEvent event) {
        nextEvent = event;
        while (nextEvent != MessageExchangeEvent.SUCCESS && nextEvent != MessageExchangeEvent.FAILURE) {
            //logger.log("MEI " + mei.getDescriptor() + " will run event " + nextEvent.name());
            switch (nextEvent) {
                case SEND_REQUEST:
                    logOutgoingMessage("");
                    nextEvent = supervisor.sendMessage(buildRequest()) ?
                            MessageExchangeEvent.SUCCESS : MessageExchangeEvent.FAILURE;
                    break;
            }
        }

        supervisor.messageExchangeDone(mei, nextEvent == MessageExchangeEvent.SUCCESS);
    }

    private Message buildRequest() {
        Message message = new StartProcessRequest();
        message.setSequenceNumber((byte) 49);
        message.setData(prepareDummyStartProcessMessageData());
        return message;
    }

    private boolean processResponse(Message responseMessage) {
        return true;
    }

    private static byte[] prepareDummyStartProcessMessageData() {
        byte[] out = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write("2".getBytes("US-ASCII"));
            baos.write("32".getBytes("US-ASCII"));
            out = baos.toByteArray();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return out;
    }
}
