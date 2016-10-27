package com.spire.posmate.protocol.messagexchange;

import com.spire.posmate.Logger;
import com.spire.posmate.message.Message;
import com.spire.posmate.protocol.MEI;
import com.spire.posmate.protocol.MessageExchangeSupervisor;

public abstract class MessageExchange {

    protected MEI mei;
    protected MessageExchangeSupervisor supervisor;
    protected MessageExchangeEvent nextEvent;

    protected Logger logger;

    public MessageExchange(MessageExchangeSupervisor supervisor, Logger logger) {
        this.supervisor = supervisor;
        this.logger = logger;
    }

    public void startExchangeWithEvent(MessageExchangeEvent event) {

    }

    public void startExchangeWithMessage(Message event) {

    }

    protected void logOutgoingMessage(CharSequence content) {
        logger.log("To PosMate: " + mei + " " + content);
    }

    protected void logIncomingMessage(CharSequence content) {
        logger.log("From PosMate: " + mei + " " + content);
    }
}
