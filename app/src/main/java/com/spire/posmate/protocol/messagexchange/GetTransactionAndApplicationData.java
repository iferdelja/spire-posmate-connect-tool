package com.spire.posmate.protocol.messagexchange;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Message;
import com.spire.posmate.message.ResponseMessage;
import com.spire.posmate.message.StatusReportResponse;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;

import java.io.ByteArrayOutputStream;

public class GetTransactionAndApplicationData extends MessageExchange {

    Message request;

    public GetTransactionAndApplicationData(MessageExchangeSupervisor supervisor, Logger logger) {
        super(supervisor, logger);
        mei = MEI.GET_TRANSACTION_AND_APPLICATION_DATA;
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

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setIdentifier(new byte[]{0x34, 0x32});
        responseMessage.setSequenceNumber(request.getSequenceNumber());
        responseMessage.setResponse((byte) 48);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       // baos.write();
        return responseMessage;
    }

    private boolean processRequest(Message requestMessage) {
        logIncomingMessage(Message.parseField(requestMessage.getData()));
        return true;
    }

}
