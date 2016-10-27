package com.spire.posmate.protocol;

import android.os.Handler;
import android.util.Log;

import com.spire.posmate.Logger;
import com.spire.posmate.PosmateDevice;
import com.spire.posmate.message.Message;
import com.spire.posmate.protocol.messagexchange.GetTransactionAndApplicationData;
import com.spire.posmate.protocol.messagexchange.MessageExchange;
import com.spire.posmate.protocol.messagexchange.MessageExchangeEvent;
import com.spire.posmate.protocol.messagexchange.Level2Init;
import com.spire.posmate.protocol.messagexchange.StandBy;
import com.spire.posmate.protocol.messagexchange.StartProcess;
import com.spire.posmate.protocol.messagexchange.StatusMessage;
import com.spire.posmate.protocol.messagexchange.TerminateTransaction;

public class PaymentProcessor implements MessageExchangeSupervisor {

    private PosmateDevice device;
    private Logger logger;

    public PaymentProcessor(PosmateDevice device, Logger logger) {
        this.device = device;
        this.logger = logger;
    }

    public void processPayment() {
        startNextMessageExchange(MEI.LEVEL2_INIT, MessageExchangeEvent.SEND_REQUEST);
    }

    private void startNextMessageExchange(MEI mei, MessageExchangeEvent startEvent) {
        Log.d(Logger.LOG_TAG, "PP start exchange " + mei.getDescriptor());
        MessageExchange exchange = null;
        switch (mei) {
            case LEVEL2_INIT:
                exchange = new Level2Init(this, logger);
                break;
            case STANDBY:
                exchange = new StandBy(this, logger);
                break;
            case START_PROCESS:
                exchange = new StartProcess(this, logger);

                break;
        }
        if (exchange != null) {
            logger.leaveSomeSpace();
            exchange.startExchangeWithEvent(startEvent);
        }
    }

    private void startNextMessageExchange(MEI mei, Message message) {
        Log.d(Logger.LOG_TAG, "\nPP start exchange " + mei.getDescriptor());
        MessageExchange exchange = null;
        switch (mei) {
            case STATUS_MESSAGE:
                exchange = new StatusMessage(this, logger);
                break;
            case TERMINATE_TRANSACTION:
                exchange = new TerminateTransaction(this, logger);
                break;
            case GET_TRANSACTION_AND_APPLICATION_DATA:
                exchange = new GetTransactionAndApplicationData(this, logger);
                break;
        }
        if (exchange != null) {
            logger.leaveSomeSpace();
            exchange.startExchangeWithMessage(message);
        }
    }


    @Override
    public void messageExchangeDone(MEI mei, boolean withSuccess) {
        if (mei == null) {
            logger.log("PP Missing MEI to identify exchange. Check your MEI implementations!");
            return;
        }

        if (!withSuccess) {
            logger.log("PP " + mei.getDescriptor() + " failed");
            return;
        }


        switch (mei) {
            case STANDBY:
                startNextMessageExchange(MEI.LEVEL2_INIT, MessageExchangeEvent.SEND_REQUEST);
                break;
            case LEVEL2_INIT:
                startNextMessageExchange(MEI.START_PROCESS, MessageExchangeEvent.SEND_REQUEST);
                break;
            case START_PROCESS:
            case STATUS_MESSAGE:
                receiveMessageForNextMessageExchange();
                break;
            case TERMINATE_TRANSACTION:
                //receiveMessageForNextMessageExchange();
                device.disconnect();
                break;
        }
    }

    private void receiveMessageForNextMessageExchange() {
        Message message = device.receiveMessage();
        if (message == null) {
            Log.d(Logger.LOG_TAG, "PP received no message. ");
            return;
        }

        final int identifier = Message.getMessageIdentifier(message.getIdentifier());
        Log.d(Logger.LOG_TAG, "PP received MEI " + identifier);
        if (MEI.STATUS_MESSAGE.getIdentifier() == identifier) {
            startNextMessageExchange(MEI.STATUS_MESSAGE, message);
        } else if (MEI.TERMINATE_TRANSACTION.getIdentifier() == identifier) {
                startNextMessageExchange(MEI.TERMINATE_TRANSACTION, message);
        } else if (MEI.GET_TRANSACTION_AND_APPLICATION_DATA.getIdentifier() == identifier) {
            startNextMessageExchange(MEI.GET_TRANSACTION_AND_APPLICATION_DATA, message);
        } else {
            Log.d(Logger.LOG_TAG, "PP dont know how to handle MEI " + identifier);
        }
    }

    @Override
    public boolean sendMessage(Message message) {
        return device.sendMessage(message);
    }

    @Override
    public Message receiveMessage() {
        return device.receiveMessage();
    }
}
