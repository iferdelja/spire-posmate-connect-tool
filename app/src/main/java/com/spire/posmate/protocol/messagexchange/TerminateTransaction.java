package com.spire.posmate.protocol.messagexchange;

import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Message;
import com.spire.posmate.message.ResponseMessage;
import com.spire.posmate.message.StatusReportResponse;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;

public class TerminateTransaction extends MessageExchange {

    Message request;

    public TerminateTransaction(MessageExchangeSupervisor supervisor, Logger logger) {
        super(supervisor, logger);
        mei = MEI.TERMINATE_TRANSACTION;
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
                    //logger.log("TO POSMATE: " + mei);
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
        //if ("A0".equals(Message.parseField(request.getData()))) {
        int response = 48;
            statusReportResponse.setResponse((byte) response);
//        } else {
//            statusReportResponse.setResponse((byte) 51);
//        }

        logOutgoingMessage(Message.responseDescriptorForId(response - 48));

        return statusReportResponse;
    }

    // Response OK = 0, Response Abort = 3

    private boolean processRequest(Message requestMessage) {
//        Log.d(Logger.LOG_TAG, "REQ,SEQ=" + requestMessage.getSequenceNumber()
//                + " STATUS:" + Message.parseField(requestMessage.getData()));
        logIncomingMessage("Reason: " + getReasonDescriptor(Message.parseField(requestMessage.getData())));
        return true;
    }

    private static String getReasonDescriptor(String status) {
        switch (status) {
            case "11":
                return "(EPOS) Transaction terminated";
            default:
                return status;
        }
    }

}
