package com.spire.posmate.protocol.messagexchange;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Message;
import com.spire.posmate.message.StandByRequest;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;

public class StandBy extends MessageExchange {

    private Message receivedResponse;

    public StandBy(MessageExchangeSupervisor supervisor, Logger logger) {
        super(supervisor, logger);
        mei = MEI.STANDBY;
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
                    logger.log("FROM POSMATE: OK");
                    receivedResponse = supervisor.receiveMessage();
                    nextEvent = receivedResponse != null ?
                            MessageExchangeEvent.SUCCESS : MessageExchangeEvent.FAILURE;
                    break;
//                case PROCESS_RESPONSE:
//                    nextEvent = processResponse(receivedResponse) ?
//                            MessageExchangeEvent.SUCCESS : MessageExchangeEvent.FAILURE;
//                    break;
            }
        }

        supervisor.messageExchangeDone(mei, nextEvent == MessageExchangeEvent.SUCCESS);
    }

    private Message buildRequest() {
        Message message = new StandByRequest();
        message.setSequenceNumber((byte) 49);
        return message;
    }

    private boolean processResponse(Message responseMessage) {
        return true;
    }
}
