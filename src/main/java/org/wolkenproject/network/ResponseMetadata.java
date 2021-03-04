package org.wolkenproject.network;

import org.wolkenproject.network.messages.ResponseMessage;

public interface ResponseMetadata {
    public static final class ValidationBits {
    }

    int isResponseValid(Message responseMessage);
}
