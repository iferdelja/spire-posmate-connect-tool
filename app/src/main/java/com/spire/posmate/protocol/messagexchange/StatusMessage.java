package com.spire.posmate.protocol.messagexchange;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Message;
import com.spire.posmate.message.ResponseMessage;
import com.spire.posmate.message.StatusReportResponse;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;

public class StatusMessage extends MessageExchange {

    Message request;

    public StatusMessage(MessageExchangeSupervisor supervisor, Logger logger) {
        super(supervisor, logger);
        mei = MEI.STATUS_MESSAGE;
    }

    public void startExchangeWithMessage(Message request) {
        this.request = request;
        nextEvent = MessageExchangeEvent.PROCESS_REQUEST;

        while (nextEvent != MessageExchangeEvent.SUCCESS && nextEvent != MessageExchangeEvent.FAILURE) {
            //logger.log("MEI " + mei.getDescriptor() + " will run event " + nextEvent.name());
            switch (nextEvent) {
                case PROCESS_REQUEST:
                    nextEvent = processRequest(request) ?
                            MessageExchangeEvent.SEND_RESPONSE : MessageExchangeEvent.FAILURE;
                    break;
                case SEND_RESPONSE:
                    nextEvent = supervisor.sendMessage(buildResponse(this.request)) ?
                            MessageExchangeEvent.SUCCESS : MessageExchangeEvent.FAILURE;
//                    if ("A4".equals(Message.parseField(request.getData()))) {
//                        nextEvent = MessageExchangeEvent.FAILURE;
//                    }
                    break;
            }
        }

        supervisor.messageExchangeDone(mei, nextEvent == MessageExchangeEvent.SUCCESS);
    }

    private Message buildResponse(Message request) {

        ResponseMessage statusReportResponse = new StatusReportResponse();
        statusReportResponse.setIdentifier(new byte[]{0x34, 0x39});
        statusReportResponse.setSequenceNumber(request.getSequenceNumber());
        int response;
        //if ("A0".equals(Message.parseField(request.getData()))) {
            response = 48;
            statusReportResponse.setResponse((byte) response);
//        } else {
//            response = 51;
//            statusReportResponse.setResponse((byte) response);
//        }

        logOutgoingMessage(Message.responseDescriptorForId(response - 48));

        return statusReportResponse;
    }

    private boolean processRequest(Message requestMessage) {
        logIncomingMessage(getStatusDescriptor(Message.parseField(requestMessage.getData())));
        return true;
    }

    private static String getStatusDescriptor(String status) {
        switch (status) {
            case "A0":
                return "Card entry prompted";
            case "A1":
                return "Smart card inserted";
            case "A4":
                return "Card entry bypassed";
            case "B0":
                return "Application selection started";
            default:
                return status;
        }
    }

}
