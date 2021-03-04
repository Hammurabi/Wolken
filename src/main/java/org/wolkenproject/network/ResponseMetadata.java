package org.wolkenproject.network;

import org.wolkenproject.network.messages.ResponseMessage;

public interface ResponseMetadata {
    public static final class ValidationBits {
        public static final int
            FullResponse    = 0,
            PartialResponse = 1,
            InvalidResponse = 2;
        ;
    }

    int isResponseValid(Message responseMessage);
}
