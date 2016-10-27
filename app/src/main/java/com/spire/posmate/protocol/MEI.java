package com.spire.posmate.protocol;

/**
 * Message Exchange Identifier
 */
public enum MEI {

    LEVEL2_INIT(12, "LEVEL2_INIT"),
    STANDBY(13, "STANDBY"),
    START_PROCESS(41, "START_PROCESS"),
    GET_TRANSACTION_AND_APPLICATION_DATA(42, "GET_TRANSACTION_AND_APPLICATION_DATA"),
    TERMINATE_TRANSACTION(48, "TERMINATE_TRANSACTION"),
    STATUS_MESSAGE(49, "STATUS_MESSAGE");

    private int identifier;
    private String descriptor;

    MEI(int identifier, String descriptor) {
        this.identifier = identifier;
        this.descriptor = descriptor;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String toString() {
        return name() + "(" + getIdentifier() + ")";
    }

}
