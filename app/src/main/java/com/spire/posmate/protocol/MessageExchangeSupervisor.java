package com.spire.posmate.protocol;

import com.spire.posmate.message.Message;

public interface MessageExchangeSupervisor {
    void messageExchangeDone(MEI mei, boolean withSuccess);
    boolean sendMessage(Message message);
    Message receiveMessage();
}
